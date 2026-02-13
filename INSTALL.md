# Quick Installation Guide - MCMS

**Platform**: Windows, macOS, Kubuntu 24 (and other Linux)

---

## Step 1: Check Prerequisites

**Run the prerequisite checker for your platform:**

### Windows
```cmd
check-prerequisites.bat
```

### macOS / Linux / Kubuntu 24
```bash
./check-prerequisites.sh
```

This will check for all required software and show what's missing.

---

## Step 2: Install Missing Prerequisites

### Windows

#### Java 21
```cmd
# Download and install from:
https://adoptium.net/temurin/releases/?version=21

# Or use winget:
winget install EclipseAdoptium.Temurin.21.JDK

# Set JAVA_HOME (optional but recommended):
# System Properties → Environment Variables → New
# Variable: JAVA_HOME
# Value: C:\Program Files\Eclipse Adoptium\jdk-21.0.x.x-hotspot
```

#### Maven 3.9+
```cmd
# Download and install from:
https://maven.apache.org/download.cgi

# Or use chocolatey:
choco install maven

# Or use scoop:
scoop install maven
```

#### Node.js 18+ & npm
```cmd
# Download and install from:
https://nodejs.org/ (choose LTS version)

# Or use winget:
winget install OpenJS.NodeJS.LTS
```

#### JHipster CLI
```cmd
npm install -g generator-jhipster
```

#### Git
```cmd
# Download and install from:
https://git-scm.com/downloads

# Or use winget:
winget install Git.Git
```

---

### macOS

#### Homebrew (if not installed)
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

#### Java 21
```bash
brew install openjdk@21

# Add to PATH (add to ~/.zshrc or ~/.bash_profile):
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH=$JAVA_HOME/bin:$PATH
```

#### Maven 3.9+
```bash
brew install maven
```

#### Node.js 18+ & npm
```bash
brew install node@18
# or for latest LTS:
brew install node
```

#### JHipster CLI
```bash
npm install -g generator-jhipster
```

#### Git
```bash
brew install git
```

---

### Kubuntu 24 (and Ubuntu-based distributions)

#### Java 21
```bash
sudo apt update
sudo apt install openjdk-21-jdk

# Set JAVA_HOME (add to ~/.bashrc):
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

#### Maven 3.9+
```bash
sudo apt install maven

# Verify version (Ubuntu 24 should have 3.9+):
mvn -version

# If older version, download manually from:
# https://maven.apache.org/download.cgi
```

#### Node.js 18+ & npm
```bash
# Using NodeSource repository (recommended):
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# Verify:
node -v
npm -v
```

#### JHipster CLI
```bash
npm install -g generator-jhipster

# If permission errors, fix npm global directory:
mkdir ~/.npm-global
npm config set prefix '~/.npm-global'
echo 'export PATH=~/.npm-global/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# Then retry:
npm install -g generator-jhipster
```

#### Git
```bash
sudo apt install git

# Configure:
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

---

## Step 3: Verify Installation

**Run the prerequisite checker again:**

### Windows
```cmd
check-prerequisites.bat
```

### macOS / Linux / Kubuntu 24
```bash
./check-prerequisites.sh
```

**Expected output:**
```
✓ PASS Java 21 is installed
✓ PASS JAVA_HOME is set correctly
✓ PASS Maven 3.9+ is installed
✓ PASS Node.js 18+ is installed
✓ PASS npm 9+ is installed
✓ PASS JHipster CLI is installed
✓ PASS Git is installed
✓ PASS Maven build successful

✓ ALL CHECKS PASSED
```

---

## Step 4: Build Project

```bash
# Navigate to project directory
cd /path/to/dsl_dev_mush

# Build all modules
mvn clean install

# Expected: BUILD SUCCESS in ~4 seconds
```

---

## Step 5: Read Context

```bash
# 1. Read agent memories (MUST READ FIRST)
cat dev-docs/AGENT_MEMORIES.md

# 2. Read collaboration rules
cat docs/AGENTS.md

# 3. Read implementation plan
cat dev-docs/01-dsl-first-implementation-plan.md

# 4. Check current status
git log --oneline -n 5
```

---

## Troubleshooting

### Java version conflicts
```bash
# Windows
where java
# Check which java.exe is first in PATH

# macOS
which java
/usr/libexec/java_home -V  # List all installed JDKs

# Linux
which java
update-alternatives --config java  # Choose Java 21
```

### JAVA_HOME not working
```bash
# Windows (PowerShell)
$env:JAVA_HOME
[System.Environment]::GetEnvironmentVariable('JAVA_HOME', 'Machine')

# macOS / Linux
echo $JAVA_HOME
```

### npm permission errors (Linux)
```bash
# Fix npm global directory permissions:
mkdir ~/.npm-global
npm config set prefix '~/.npm-global'
echo 'export PATH=~/.npm-global/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

### Maven build fails
```bash
# Clear corrupted cache:
rm -rf ~/.m2/repository/com/mcms  # macOS/Linux
rmdir /s /q %USERPROFILE%\.m2\repository\com\mcms  # Windows

# Force update dependencies:
mvn clean install -U
```

---

## Platform-Specific Notes

### Windows
- Use PowerShell or Git Bash (not CMD)
- Environment variables: System Properties → Environment Variables
- Restart terminal after installing software

### macOS
- Requires Xcode Command Line Tools: `xcode-select --install`
- Use Homebrew for package management
- Add environment variables to `~/.zshrc` (or `~/.bash_profile` for bash)

### Kubuntu 24 / Ubuntu
- Use `apt` package manager
- Add environment variables to `~/.bashrc`
- May need `sudo` for installations
- npm global packages: Fix permissions (see troubleshooting)

---

## Next Steps

After all checks pass:

1. ✅ **Read Documentation**
   - `dev-docs/AGENT_MEMORIES.md` - Full context
   - `docs/AGENTS.md` - Collaboration rules
   - `README.md` - Project overview

2. ✅ **Build Project**
   - `mvn clean install`

3. ✅ **Continue Development**
   - Check current phase: `git log --oneline -n 1`
   - Follow plan in `dev-docs/01-dsl-first-implementation-plan.md`

---

## Getting Help

**Build Issues**: See `docs/PREMORTEM.md` Risk section
**Process Questions**: See `docs/AGENTS.md`
**Detailed Setup**: See `dev-docs/SETUP_GUIDE.md`
**JHipster Issues**: https://www.jhipster.tech/
**ANTLR Issues**: https://github.com/antlr/antlr4/blob/master/doc/index.md

---

**Last Updated**: 2026-02-12
**Platforms Tested**: Windows 11, macOS 14, Kubuntu 24.04
