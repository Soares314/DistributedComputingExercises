package Ex5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {

    private static final String URLS_FILE = "src/Ex5/SearchURLS";
    private static final String WORDS_FILE = "src/Ex5/SearchWords";
    private static final String RESULTS_FILE = "src/Ex5/Results.txt";

    private static final Pattern HREF_PATTERN = Pattern.compile("href=[\"'](https?://[^\"'>#]+)[\"']", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Número máximo de URLs a pesquisar:");
            int maxUrls = Integer.parseInt(scanner.nextLine().trim());

            System.out.println("Número máximo de threads:");
            int maxThreads = Integer.parseInt(scanner.nextLine().trim());

            new WebCrawler().run(maxUrls, maxThreads);
        } catch (Exception e) {
            System.err.println("Erro na execução: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void run(int maxUrls, int maxThreads) throws IOException, InterruptedException {
        Set<String> searchWords = loadWords(PathsToString(WORDS_FILE));

        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
        List<String> initialUrls = Files.readAllLines(Path.of(URLS_FILE));
        for (String u : initialUrls) {
            String s = u.trim();
            if (!s.isEmpty()) queue.offer(s);
        }

        Set<String> visited = ConcurrentHashMap.newKeySet();
        AtomicInteger processed = new AtomicInteger(0);

        Files.writeString(Path.of(RESULTS_FILE), "", StandardCharsets.UTF_8);

        ExecutorService pool = Executors.newFixedThreadPool(Math.max(1, maxThreads));

        for (int i = 0; i < Math.max(1, maxThreads); i++) {
            pool.submit(() -> {
                while (true) {
                    if (processed.get() >= maxUrls) break;
                    String url = null;
                    try {
                        url = queue.poll(2, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    if (url == null) {
                        if (processed.get() >= maxUrls) break;
                        continue;
                    }

                    boolean firstVisit = visited.add(url);
                    if (!firstVisit) continue;

                    int current = processed.incrementAndGet();
                    if (current > maxUrls) break;

                    try {
                        String content = fetchUrl(url);
                        if (content == null) continue;

                        Set<String> foundLinks = extractLinks(content);
                        for (String link : foundLinks) {
                            if (!visited.contains(link)) {
                                queue.offer(link);
                                appendLineToFile(URLS_FILE, link);
                            }
                        }

                        String text = stripTags(content).toLowerCase(Locale.ROOT);
                        var counts = countWords(text, searchWords);
                        writeResult(url, counts);

                        System.out.println("[OK] " + url + " (#" + current + ")");

                    } catch (Exception e) {
                        System.err.println("Erro ao processar " + url + ": " + e.getMessage());
                    }

                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.MINUTES);

        System.out.println("Crawling finalizado. Resultados em: " + RESULTS_FILE);
    }

    private static Set<String> loadWords(String path) throws IOException {
        Set<String> set = new HashSet<>();
        List<String> lines = Files.readAllLines(Path.of(path));
        for (String l : lines) {
            String w = l.trim().toLowerCase(Locale.ROOT);
            if (!w.isEmpty()) set.add(w);
        }
        return set;
    }

    private static String fetchUrl(String urlStr) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("User-Agent", "SimpleJavaCrawler/1.0");
            int code = conn.getResponseCode();
            if (code >= 400) {
                System.err.println("HTTP " + code + " for " + urlStr);
                return null;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                return sb.toString();
            }
        } catch (Exception e) {
            System.err.println("Erro fetch " + urlStr + ": " + e.getMessage());
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static Set<String> extractLinks(String html) {
        Set<String> links = new HashSet<>();
        Matcher m = HREF_PATTERN.matcher(html);
        while (m.find()) {
            String link = m.group(1).trim();
            // normalize: remove trailing slash duplicates
            if (link.endsWith("/")) link = link.substring(0, link.length() - 1);
            links.add(link);
        }
        return links;
    }

    private static String stripTags(String html) {
        String noScript = html.replaceAll("(?s)<script.*?>.*?</script>", " ");
        noScript = noScript.replaceAll("(?s)<style.*?>.*?</style>", " ");
    
        String text = noScript.replaceAll("<[^>]+>", " ");
        
        text = text.replaceAll("&nbsp;", " ").replaceAll("&amp;", "&");
        return text;
    }

    private static java.util.Map<String, Integer> countWords(String text, Set<String> searchWords) {
        java.util.Map<String, Integer> map = new java.util.HashMap<>();
        String[] tokens = text.split("\\W+");
        for (String t : tokens) {
            String w = t.trim().toLowerCase(Locale.ROOT);
            if (w.isEmpty()) continue;
            if (searchWords.contains(w)) {
                map.put(w, map.getOrDefault(w, 0) + 1);
            }
        }
        
        for (String w : searchWords) map.putIfAbsent(w, 0);
        return map;
    }

    private static synchronized void appendLineToFile(String path, String line) {
        try {
            Files.writeString(Path.of(path), line + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Erro ao anexar em " + path + ": " + e.getMessage());
        }
    }

    private static synchronized void writeResult(String url, java.util.Map<String, Integer> counts) {
        StringBuilder sb = new StringBuilder();
        sb.append("URL: ").append(url).append(System.lineSeparator());
        for (var e : counts.entrySet()) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        try {
            Files.writeString(Path.of(RESULTS_FILE), sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Erro ao escrever resultados: " + e.getMessage());
        }
    }

    private static String PathsToString(String p) {
        return p;
    }

}
