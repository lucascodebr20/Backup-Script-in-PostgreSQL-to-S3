package org.scriptbakcup;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScriptBackup {

    private S3Client createClient() {
        String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");

        AwsBasicCredentials awsCredential = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.US_EAST_2)  // Set the region according to your bucket configuration
                .credentialsProvider(StaticCredentialsProvider.create(awsCredential))
                .build();
    }

    public void start() {

        String nameDataBase = "enter the database name";;
        String bucketName = "enter the bucket name";
        String nameFileBackup = "enter the backup file name";
        int backupIntervalMinutes = 360; // Define the backup interval in minutes
        int limitBackup = 3; /* Backup limit, set 0 for unlimited.
        If the value is greater than 0, it will delete the oldest backup
        to store the new one once the limit is reached. */

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable backupTask = () -> {
            try {
                String nameFile = nameFileBackup + LocalDateTime.now() + ".bkp";
                String filePath = generateBackup(nameDataBase, nameFile);

                if (limitBackup <= 0) {
                    uploadS3AWS(filePath, nameFileBackup, bucketName);
                } else {
                    uploadBackupWithLimit(nameFileBackup,filePath, bucketName, nameFile, limitBackup);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        scheduler.scheduleAtFixedRate(backupTask, 0, backupIntervalMinutes, TimeUnit.MINUTES);
    }


    public void uploadBackupWithLimit (String nameFileBackup, String filePath,
                                      String bucketName, String nameFile, int maxBackups) {

        try (S3Client s3 = createClient()) {

            uploadS3AWS(filePath, nameFile, bucketName);

            ListObjectsV2Response listResponse = s3.listObjectsV2(ListObjectsV2Request.builder().bucket(bucketName).build());
            List<S3Object> objects = listResponse.contents();
            List<S3Object> backups = objects.stream()
                    .filter(obj -> obj.key().contains(nameFileBackup))
                    .toList();

            int excess = backups.size() - maxBackups;
            if (excess > 0) {
                backups.stream()
                        .sorted(Comparator.comparing(S3Object::lastModified))
                        .limit(excess)
                        .forEach(obj -> {
                            s3.deleteObject(DeleteObjectRequest.builder()
                                    .bucket(bucketName)
                                    .key(obj.key())
                                    .build());
                            System.out.println("Old backup deleted: " + obj.key());
                        });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String generateBackup(String nameDataBase, String nameFileBackup) {

        System.out.println("Backup start: " + LocalDateTime.now());

        // If you are using Windows, replace /tmp with a folder of your choice
        String pathFile = "/tmp/" + nameFileBackup;

        String[] cmd = {
                "pg_dump", "-U",
                System.getenv("DB_USERNAME"),
                "-d", nameDataBase, "-f", pathFile,
                "-W"};

        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(process.getOutputStream()));
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(process.getInputStream()))) {

                writer.write(System.getenv("DB_PASSWORD"));
                writer.newLine();
                writer.flush();

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Backup created! " + LocalDateTime.now());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return pathFile;
    }

    private void uploadS3AWS(String filePath, String nameFileBackup, String bucketName) {
        try (S3Client s3 = createClient()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(nameFileBackup)
                    .build();

            s3.putObject(putObjectRequest, RequestBody.fromFile(Paths.get(filePath)));
            System.out.println("File sent to S3!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
