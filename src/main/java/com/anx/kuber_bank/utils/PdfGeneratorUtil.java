package com.anx.kuber_bank.utils;

import com.anx.kuber_bank.entity.Transaction;
import com.hubspot.jinjava.Jinjava;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jinjava.org.jsoup.Jsoup;
import jinjava.org.jsoup.helper.W3CDom;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class PdfGeneratorUtil {
    private static final Logger logger = LoggerFactory.getLogger(PdfGeneratorUtil.class);
    public static final String PDF = ".pdf";
    public static final String LIGHT_FONT = "fonts/Poppins-Light.ttf";
    public static final String BOLD_FONT = "fonts/Poppins-Bold.ttf";
    public static final String UNICODE_FONT = "fonts/Arial-Unicode-Font.ttf";
    public static final String ARIAL_UNICODE_MS = "Arial Unicode MS";
    public static final String FONTS = "fonts";

    public File generatePDF(String templateId, String templateName, Map<String, Object> templateData) {
        try {
            for (Map.Entry<String, Object> entry : templateData.entrySet()) {
                System.out.println(entry.getKey() +", " + entry.getValue());
            }
            String templateContent = FileUtils.readFileToString(ResourceUtils.getFile(
                    ResourceUtils.CLASSPATH_URL_PREFIX + String.format("templates/%s.html", templateName)),
                    StandardCharsets.UTF_8);
            Jinjava jinjava = new Jinjava();
            String htmlWithData = jinjava.render(templateContent, templateData);
            return generatePDF(templateId, templateName, htmlWithData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File generatePDF(String templateId, String templateName, String htmlWithData) {
        try {
            File templateOutputFile = File.createTempFile(templateName + "_" + templateId, PDF);
            render(htmlWithData, templateOutputFile.getAbsolutePath());
            logger.info("Generated pdf document at location {} for template {}", templateOutputFile.getAbsolutePath(), templateName);
            return templateOutputFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void render(String html, String outputFilePath) throws IOException {
        try (OutputStream os = Files.newOutputStream(Paths.get(outputFilePath))) {
            File lightFont = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + LIGHT_FONT);
            File boldFont = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + BOLD_FONT);
            File unicodeFont = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + UNICODE_FONT);
            Document document = new W3CDom().fromJsoup(Jsoup.parse(html));
            new PdfRendererBuilder()
                    .useFastMode()
                    .withW3cDocument(document, "/")
                    .useFont(lightFont, FONTS, 200, BaseRendererBuilder.FontStyle.NORMAL, true)
                    .useFont(boldFont, FONTS, 700, BaseRendererBuilder.FontStyle.NORMAL, true)
                    .useFont(unicodeFont, ARIAL_UNICODE_MS)
                    .toStream(os)
                    .run();
        }
    }
    public static void generateOnLocal() {
        PdfGeneratorUtil pdfGeneratorUtil = new PdfGeneratorUtil();
        Map<String, Object> data = new HashMap<>();
        Transaction transaction = Transaction.builder()
                .transactionId("12345")
                .transactionType("CREDIT")
                .accountBalance(BigDecimal.valueOf(1000))
                .status("SUCCESS")
                .amount(BigDecimal.valueOf(500))
                .createdAt(LocalDateTime.parse("2025-02-07T19:57:52.279528"))
                .build();
        Transaction transaction2 = Transaction.builder()
                .transactionId("12345")
                .transactionType("CREDIT")
                .accountBalance(BigDecimal.valueOf(1000))
                .status("SUCCESS")
                .amount(BigDecimal.valueOf(500))
                .createdAt(LocalDateTime.parse("2025-02-07T19:57:52.279528"))
                .build();
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);
        transactionList.add(transaction2);
        data.put("transactions", transactionList);
        pdfGeneratorUtil.generatePDF("", "bank_statement", data);
    }

    public static void main(String[] args) {
//        generateOnLocal();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        Date today = new Date();
        Map<String, Object> data = new HashMap<>();
        data.put("todayDate", sdf.format(today));
        System.out.println(data);
    }
}
