# SETUP GUIDE - MCMS Project

**Purpose**: Complete installation guide for setting up the development environment on any machine
**Last Updated**: 2026-02-12

---

## Overview

This guide lists all software that must be **installed** (not just Maven dependencies) to work on the MCMS project.

**TL;DR** - You need:
1. Java 21 (JDK)
2. Maven 3.9+
3. Node.js 18+ & npm 9+
4. JHipster CLI (npm package)
5. Git

---

## Required Installations

### 1. Java Development Kit (JDK 21)

**What**: Java runtime and compiler
**Why**: MCMS uses Java 21 features
**Download**: https://adoptium.net/temurin/releases/?version=21

**Installation**:
```bash
# Verify installation
java -version
# Expected: openjdk version "21.0.x"

javac -version
# Expected: javac 21.0.x
```

**Environment Variables** (set these):
- `JAVA_HOME` → Path to JDK installation (e.g., `C:\Program Files\Eclipse Adoptium\jdk-21.0.1.12-hotspot\`)
- `PATH` → Add `%JAVA_HOME%\bin` (Windows) or `$JAVA_HOME/bin` (Unix)

**Troubleshooting**:
- If `java -version` shows older version, check your `PATH`
- Multiple Java versions? Use a version manager like [jEnv](https://www.jenv.be/) or [SDKMAN!](https://sdkman.io/)

---

### 2. Apache Maven 3.9+

**What**: Build tool and dependency manager
**Why**: MCMS uses Maven for multi-module builds
**Download**: https://maven.apache.org/download.cgi

**Installation**:
```bash
# Verify installation
mvn -version
# Expected: Apache Maven 3.9.x or higher
```

**Environment Variables**:
- `M2_HOME` → Path to Maven installation (optional but recommended)
- `PATH` → Add `%M2_HOME%\bin` or direct path to `mvn` binary

**Configuration**:
- Maven settings: Default location `~/.m2/settings.xml`
- No custom settings required for this project
- Local repository: `~/.m2/repository/` (default)

**Troubleshooting**:
- If build fails with "JAVA_HOME not set", ensure Java is properly configured
- Clear corrupted dependencies: `rm -rf ~/.m2/repository/com/mcms`

---

### 3. Node.js 18+ & npm 9+

**What**: JavaScript runtime and package manager
**Why**: Required for JHipster CLI and frontend development
**Download**: https://nodejs.org/en/download/

**Installation**:
```bash
# Verify installation
node -v
# Expected: v18.x.x or higher

npm -v
# Expected: 9.x.x or higher
```

**Recommended**: Install Node.js LTS (Long Term Support) version

**Environment Variables**:
- `PATH` → Automatically added by installer
- npm global packages location: `npm config get prefix`

**Troubleshooting**:
- Permission issues on Unix/Mac: Use [nvm](https://github.com/nvm-sh/nvm) to manage Node versions
- Windows: Run installer as Administrator if needed

---

### 4. JHipster CLI

**What**: Code generator for Spring Boot + Angular/React/Vue applications
**Why**: Generates 80% of MCMS application (entities, REST APIs, frontend)
**Install Command**:
```bash
npm install -g generator-jhipster
```

**Verification**:
```bash
jhipster --version
# Expected: 8.x.x or compatible with Spring Boot 3.2.2
```

**Important Version Notes**:
- JHipster 8.x supports Java 21 and Spring Boot 3.x
- Check compatibility: https://www.jhipster.tech/installation/
- If version mismatch issues occur, pin specific version:
  ```bash
  npm install -g generator-jhipster@8.1.0
  ```

**Uninstall** (if needed):
```bash
npm uninstall -g generator-jhipster
```

**Troubleshooting**:
- `jhipster: command not found` → Check npm global bin path in `PATH`
  ```bash
  npm config get prefix
  # Add this path to your PATH environment variable
  ```
- Slow generation on Windows → Disable antivirus temporarily
- Permission errors → Use `sudo` on Unix/Mac or run terminal as Administrator on Windows

---

### 5. Git

**What**: Version control system
**Why**: Required for branching, committing, collaboration
**Download**: https://git-scm.com/downloads

**Installation**:
```bash
# Verify installation
git --version
# Expected: git version 2.x.x or higher
```

**Configuration** (required):
```bash
# Set your identity
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"

# Verify
git config --global user.name
git config --global user.email
```

**Recommended Settings**:
```bash
# Line endings (Windows)
git config --global core.autocrlf true

# Line endings (Unix/Mac)
git config --global core.autocrlf input

# Default branch name
git config --global init.defaultBranch main
```

---

## Optional but Recommended

### 6. IDE (Choose One)

#### IntelliJ IDEA (Recommended)
**What**: Java IDE with excellent Maven and ANTLR support
**Why**: Best support for Maven multi-module projects, ANTLR grammar highlighting
**Download**: https://www.jetbrains.com/idea/download/

**Edition**: Community Edition (free) is sufficient

**Plugins to Install**:
- ANTLR v4 (for grammar editing)
- Lombok (if using Lombok annotations)
- Spring Boot Assistant

#### Eclipse
**What**: Free, open-source Java IDE
**Download**: https://www.eclipse.org/downloads/

**Plugins**:
- M2E (Maven integration) - usually pre-installed
- Spring Tools 4

#### VS Code
**What**: Lightweight code editor
**Download**: https://code.visualstudio.com/

**Extensions**:
- Java Extension Pack
- Spring Boot Extension Pack
- Maven for Java

---

### 7. Database Tools (Optional)

**H2 Console**:
- Built into Spring Boot
- Access at: http://localhost:8080/h2-console
- No installation needed

**PostgreSQL** (for production):
- Download: https://www.postgresql.org/download/
- Only needed if deploying beyond development

**DBeaver** (GUI):
- Download: https://dbeaver.io/download/
- Universal database tool (supports H2, PostgreSQL, MySQL, etc.)

---

## Project-Specific Setup

### Clone Repository
```bash
# If starting fresh on new PC
cd /path/to/your/workspace
git clone <repository-url> dsl_dev_mush
cd dsl_dev_mush
```

### Verify Environment
```bash
# Check all prerequisites
java -version    # Should show 21.x.x
mvn -version     # Should show 3.9.x or higher
node -v          # Should show v18.x.x or higher
npm -v           # Should show 9.x.x or higher
jhipster --version  # Should show 8.x.x
git --version    # Should show 2.x.x or higher
```

### First Build
```bash
# From project root
mvn clean install

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Total time: ~4 seconds
```

**If build fails**:
1. Check Java version: `java -version`
2. Check JAVA_HOME: `echo $JAVA_HOME` (Unix) or `echo %JAVA_HOME%` (Windows)
3. Clear Maven cache: `mvn dependency:purge-local-repository`
4. Try again: `mvn clean install -U` (force update dependencies)

---

## Environment Variables Summary

### Windows
```cmd
# Set in System Properties → Environment Variables
JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.1.12-hotspot
PATH=%JAVA_HOME%\bin;%PATH%
PATH=C:\Program Files\Apache\maven-3.9.6\bin;%PATH%
PATH=%APPDATA%\npm;%PATH%
```

### Unix/Mac/Linux
```bash
# Add to ~/.bashrc or ~/.zshrc
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH=$JAVA_HOME/bin:$PATH
export PATH=/opt/maven/bin:$PATH
```

---

## Troubleshooting Common Issues

### Maven Build Fails
**Symptom**: `mvn clean install` errors
**Solutions**:
1. Verify Java 21: `java -version`
2. Check JAVA_HOME is set
3. Clear local repository: `rm -rf ~/.m2/repository/com/mcms`
4. Update dependencies: `mvn clean install -U`

### ANTLR Parser Not Generated
**Symptom**: `TestDSLParser.java` not found
**Solutions**:
1. Run: `cd mcms-dsl && mvn generate-sources`
2. Check ANTLR plugin in `mcms-dsl/pom.xml`
3. Verify grammar file exists: `mcms-dsl/src/main/antlr4/com/mcms/dsl/TestDSL.g4`

### JHipster Command Not Found
**Symptom**: `jhipster: command not found`
**Solutions**:
1. Reinstall: `npm install -g generator-jhipster`
2. Check npm global path: `npm config get prefix`
3. Add npm global bin to PATH
4. Restart terminal/command prompt

### Git Line Ending Issues
**Symptom**: Git shows all files as modified (CRLF vs LF)
**Solutions**:
```bash
# Windows
git config --global core.autocrlf true

# Unix/Mac
git config --global core.autocrlf input

# Reset repository
git rm --cached -r .
git reset --hard
```

---

## Quick Start (After Setup)

```bash
# 1. Clone and navigate
cd /path/to/workspace
git clone <repo-url> dsl_dev_mush
cd dsl_dev_mush

# 2. Checkout correct branch
git checkout feature/phase-0-foundation

# 3. Build project
mvn clean install

# 4. Read agent memories
cat dev-docs/AGENT_MEMORIES.md

# 5. Read plan
cat dev-docs/01-dsl-first-implementation-plan.md

# 6. Continue from current phase
# (Check commit history to see where you left off)
git log --oneline -n 5
```

---

## Platform-Specific Notes

### Windows
- Use PowerShell or Git Bash (not CMD)
- Path separators: backslash `\`
- Environment variables: `%VAR_NAME%`
- Line endings: CRLF (handled by Git)

### macOS
- Install Xcode Command Line Tools: `xcode-select --install`
- Use Homebrew for package management:
  ```bash
  brew install openjdk@21
  brew install maven
  brew install node
  ```
- Environment variables in `~/.zshrc` (or `~/.bash_profile`)

### Linux
- Use package manager (apt, yum, dnf, etc.):
  ```bash
  # Ubuntu/Debian
  sudo apt install openjdk-21-jdk maven nodejs npm git

  # Fedora/RHEL
  sudo dnf install java-21-openjdk-devel maven nodejs npm git
  ```
- Environment variables in `~/.bashrc`

---

## Verification Checklist

Before starting development, verify:

- [ ] Java 21 installed and in PATH
- [ ] JAVA_HOME environment variable set
- [ ] Maven 3.9+ installed and in PATH
- [ ] Node.js 18+ installed
- [ ] npm 9+ installed
- [ ] JHipster CLI installed globally
- [ ] Git installed and configured (name, email)
- [ ] Project builds successfully: `mvn clean install`
- [ ] ANTLR generates parsers: `cd mcms-dsl && mvn generate-sources`
- [ ] Git repository initialized: `git status` works
- [ ] Can read agent memories: `cat dev-docs/AGENT_MEMORIES.md`

**If all checked** ✅ → You're ready to develop!

---

## Next Steps

1. **Read Documentation**:
   - `README.md` - Project overview
   - `dev-docs/AGENT_MEMORIES.md` - Full context
   - `docs/AGENTS.md` - Collaboration rules
   - `dev-docs/01-dsl-first-implementation-plan.md` - Implementation plan

2. **Check Current Phase**:
   ```bash
   git log --oneline -n 1
   ```

3. **Continue Development**:
   - Follow the plan for current phase
   - Commit regularly with `[Phase-N]` prefix
   - Update AGENT_MEMORIES.md with learnings

---

## Getting Help

**Build Issues**: Check `docs/PREMORTEM.md` Risk section
**Process Questions**: Read `docs/AGENTS.md`
**JHipster Issues**: https://www.jhipster.tech/
**ANTLR Issues**: https://github.com/antlr/antlr4/blob/master/doc/index.md

---

**Last Updated**: 2026-02-12
**Maintained By**: Documentation Agent
**Project Phase**: 0 (Foundation Complete)
