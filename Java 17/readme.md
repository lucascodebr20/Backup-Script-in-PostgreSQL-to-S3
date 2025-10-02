# ☕ Java – Script Backup to S3
This folder contains the Java implementation of the backup script.
It was built to be easily integrated into any Java project, using AWS SDK v2 to upload backups directly to Amazon S3.

## 📦 Dependencies
Add the following dependency to your pom.xml:

<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.21.0</version>
</dependency>

💡 If you are using Gradle, you can adapt this dependency format accordingly.

## ⚙️ Setup

Copy the script file into your project (for example: ScriptBackup.java).

Environment Variables
Configure the following environment variables:

Bash

AWS_ACCESS_KEY_ID = your_aws_access_key
AWS_SECRET_ACCESS_KEY = your_aws_secret_key
DB_USERNAME = your_database_username
DB_PASSWORD = your_database_password

Script Parameters
Adjust the parameters inside the script:

nameDataBase → Database name
bucketName → Your S3 bucket name
nameFileBackup → Prefix for backup file name
backupIntervalMinutes → Interval between backups (in minutes)
limitBackup → Number of backups to keep (set 0 for unlimited)

## ▶️ Usage

Instantiate the script when your project starts. For example:

Java

public class MainApp {
    public static void main(String[] args) {
        ScriptBackup backup = new ScriptBackup();
        backup.start(); // Starts the scheduled backup task
    }
}

Once the project runs, the script will:
Generate a PostgreSQL backup using pg_dump.
Upload it to your configured Amazon S3 bucket.
Automatically manage old backups if you set a limit.

✅ Notes

By default, backups are saved to the /tmp folder.
On Windows, you should change this path to a directory of your choice.
The script is designed to be self-contained (single file) for easy integration and inspection.









