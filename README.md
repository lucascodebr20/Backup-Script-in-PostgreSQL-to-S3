# 📦 Script Backup Collection  

This repository contains a collection of **single-file backup scripts** written in different programming languages.  Each script is designed to be **easily integrated into your project** without relying on external tools or complex setups.  

---

## 💡 Motivation  

The idea for this project came when I needed to implement backups in a project but didn’t want to rely on installing extra tools or writing system-specific scripts.  

Instead, I created a **Java version** that could be dropped directly into the project, ensuring:  

✅ **Easy integration** – a single file you can plug into your codebase.  
✅ **Transparency** – no hidden layers of abstraction; you can inspect the entire code yourself.  
✅ **Reliability** – simple, direct, and self-contained.  

---

## ⚠️ Disclaimer

**Important:** These scripts use `pg_dump` to create full database dumps and upload them to external storage.

**Sensitive data warning:** Only use these scripts if your database **does not contain sensitive or confidential information**, as the dump is **not encrypted by default**.

**Database size limitation:** These scripts are **not suitable for very large databases**, since every run produces a full dump and can heavily load your server.

**Production recommendation:** For production environments, consider PostgreSQL's **continuous backup features**, WAL-based tools (like `wal-e`/`wal-g`), or full-featured solutions like [pgBackRest](https://pgbackrest.org/) to handle **incremental backups** and **point-in-time recovery** efficiently.

These scripts are mainly intended for **small home-lab projects or non-critical databases** where simplicity i

## 📂 Structure  

Each folder contains a script written in a different language.  
Inside each folder, you will find a dedicated **README.md** with detailed usage instructions for that specific implementation.  

---

## 🎯 Goal  

The goal of this repository is to provide a set of **plug-and-play backup scripts** that can be adapted across different technologies while keeping everything:  

- ✨ Minimal  
- 📖 Clear  
- 🔧 Maintainable  

---

## 👨‍💻 Contributors  

Thanks to everyone who contributed to this project:  

- [Lucas (Creator)](https://github.com/) 

---

💬 Want to contribute? Feel free to open a PR and add your script in your favorite language!  
