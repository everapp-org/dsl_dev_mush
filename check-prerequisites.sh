#!/usr/bin/env bash

################################################################################
# MCMS Prerequisites Checker for Mac/Linux/Kubuntu
################################################################################
# Checks all required software installations, versions, and environment
# variables needed for MCMS development on macOS and Linux (including Kubuntu 24).
################################################################################

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Counters
PASS_COUNT=0
FAIL_COUNT=0
WARN_COUNT=0

# Detect OS
detect_os() {
    if [[ "$OSTYPE" == "darwin"* ]]; then
        OS="macOS"
    elif [[ -f /etc/os-release ]]; then
        . /etc/os-release
        OS="$NAME"
    else
        OS="Linux"
    fi
}

# Helper functions
print_header() {
    echo ""
    echo "============================================================================"
    echo "  $1"
    echo "============================================================================"
    echo ""
}

print_check() {
    echo -e "${BLUE}[$1/$2] $3${NC}"
}

print_pass() {
    echo -e "${GREEN}✓ PASS${NC} $1"
    ((PASS_COUNT++)) || true
}

print_fail() {
    echo -e "${RED}✗ FAIL${NC} $1"
    ((FAIL_COUNT++)) || true
}

print_warn() {
    echo -e "${YELLOW}⚠ WARN${NC} $1"
    ((WARN_COUNT++)) || true
}

print_info() {
    echo "       $1"
}

version_compare() {
    # Compare two version strings (e.g., "3.9.5" vs "3.9.0")
    # Returns 0 if $1 >= $2, 1 otherwise
    printf '%s\n' "$2" "$1" | sort -V -C
}

################################################################################
# Main Script
################################################################################

detect_os

print_header "MCMS - Mushroom Cultivation Management System"
echo "Prerequisites Checker for $OS"
echo ""

################################################################################
# 1. Check Java 21
################################################################################

print_check "1" "7" "Checking Java 21..."

if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    echo "Found: $JAVA_VERSION"

    if echo "$JAVA_VERSION" | grep -q "version \"21\."; then
        print_pass "Java 21 is installed"
    else
        print_fail "Java 21 required, but different version found"
        print_info "Download: https://adoptium.net/temurin/releases/?version=21"
        if [[ "$OS" == "macOS" ]]; then
            print_info "or install via: brew install openjdk@21"
        elif [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Kubuntu"* ]]; then
            print_info "or install via: sudo apt install openjdk-21-jdk"
        fi
    fi
else
    print_fail "Java not found in PATH"
    print_info "Download: https://adoptium.net/temurin/releases/?version=21"
    if [[ "$OS" == "macOS" ]]; then
        print_info "or install via: brew install openjdk@21"
    elif [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Kubuntu"* ]]; then
        print_info "or install via: sudo apt install openjdk-21-jdk"
    fi
fi

echo ""
echo "Checking JAVA_HOME environment variable..."
if [[ -n "$JAVA_HOME" ]]; then
    echo "Found: JAVA_HOME=$JAVA_HOME"
    if [[ -f "$JAVA_HOME/bin/java" ]]; then
        print_pass "JAVA_HOME is set correctly"
    else
        print_warn "JAVA_HOME is set but java not found at that location"
    fi
else
    print_warn "JAVA_HOME not set (recommended but not required)"
    if [[ "$OS" == "macOS" ]]; then
        print_info "Add to ~/.zshrc or ~/.bash_profile:"
        print_info "export JAVA_HOME=\$(/usr/libexec/java_home -v 21)"
    else
        print_info "Add to ~/.bashrc:"
        print_info "export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64"
    fi
fi

echo ""
echo "============================================================================"

################################################################################
# 2. Check Maven 3.9+
################################################################################

print_check "2" "7" "Checking Maven 3.9+..."

if command -v mvn &> /dev/null; then
    MAVEN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
    echo "Found: Maven $MAVEN_VERSION"

    if version_compare "$MAVEN_VERSION" "3.9.0"; then
        print_pass "Maven 3.9+ is installed"
    else
        print_warn "Maven 3.9+ recommended, found $MAVEN_VERSION"
        print_info "Download: https://maven.apache.org/download.cgi"
        if [[ "$OS" == "macOS" ]]; then
            print_info "or install via: brew install maven"
        elif [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Kubuntu"* ]]; then
            print_info "or install via: sudo apt install maven"
        fi
    fi
else
    print_fail "Maven not found in PATH"
    print_info "Download: https://maven.apache.org/download.cgi"
    if [[ "$OS" == "macOS" ]]; then
        print_info "or install via: brew install maven"
    elif [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Kubuntu"* ]]; then
        print_info "or install via: sudo apt install maven"
    fi
fi

echo ""
echo "============================================================================"

################################################################################
# 3. Check Node.js 18+
################################################################################

print_check "3" "7" "Checking Node.js 18+..."

if command -v node &> /dev/null; then
    NODE_VERSION=$(node -v)
    echo "Found: $NODE_VERSION"

    NODE_MAJOR=$(echo "$NODE_VERSION" | sed 's/v//' | cut -d. -f1)

    if [[ $NODE_MAJOR -ge 18 ]]; then
        print_pass "Node.js 18+ is installed"
    else
        print_fail "Node.js 18+ required, found $NODE_VERSION"
        print_info "Download: https://nodejs.org/"
        if [[ "$OS" == "macOS" ]]; then
            print_info "or install via: brew install node"
        elif [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Kubuntu"* ]]; then
            print_info "Install via NodeSource:"
            print_info "curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -"
            print_info "sudo apt install -y nodejs"
        fi
    fi
else
    print_fail "Node.js not found in PATH"
    print_info "Download: https://nodejs.org/"
    if [[ "$OS" == "macOS" ]]; then
        print_info "or install via: brew install node"
    elif [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Kubuntu"* ]]; then
        print_info "Install via NodeSource:"
        print_info "curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -"
        print_info "sudo apt install -y nodejs"
    fi
fi

echo ""
echo "============================================================================"

################################################################################
# 4. Check npm 9+
################################################################################

print_check "4" "7" "Checking npm 9+..."

if command -v npm &> /dev/null; then
    NPM_VERSION=$(npm -v)
    echo "Found: npm $NPM_VERSION"

    NPM_MAJOR=$(echo "$NPM_VERSION" | cut -d. -f1)

    if [[ $NPM_MAJOR -ge 9 ]]; then
        print_pass "npm 9+ is installed"
    else
        print_warn "npm 9+ recommended, found $NPM_VERSION"
        print_info "Update: npm install -g npm@latest"
    fi
else
    print_fail "npm not found in PATH"
fi

echo ""
echo "============================================================================"

################################################################################
# 5. Check JHipster CLI
################################################################################

print_check "5" "7" "Checking JHipster CLI..."

if command -v jhipster &> /dev/null; then
    JHIPSTER_VERSION=$(jhipster --version 2>&1 || echo "unknown")
    echo "Found: $JHIPSTER_VERSION"
    print_pass "JHipster CLI is installed"
else
    print_fail "JHipster CLI not found"
    print_info "Install: npm install -g generator-jhipster"
fi

echo ""
echo "============================================================================"

################################################################################
# 6. Check Git
################################################################################

print_check "6" "7" "Checking Git..."

if command -v git &> /dev/null; then
    GIT_VERSION=$(git --version | awk '{print $3}')
    echo "Found: git version $GIT_VERSION"
    print_pass "Git is installed"

    echo ""
    echo "Checking Git configuration..."

    GIT_NAME=$(git config --global user.name 2>/dev/null || echo "")
    if [[ -n "$GIT_NAME" ]]; then
        echo "  user.name: $GIT_NAME"
    else
        print_warn "Git user.name not configured"
        print_info "Set: git config --global user.name \"Your Name\""
    fi

    GIT_EMAIL=$(git config --global user.email 2>/dev/null || echo "")
    if [[ -n "$GIT_EMAIL" ]]; then
        echo "  user.email: $GIT_EMAIL"
    else
        print_warn "Git user.email not configured"
        print_info "Set: git config --global user.email \"you@example.com\""
    fi
else
    print_fail "Git not found in PATH"
    print_info "Download: https://git-scm.com/downloads"
    if [[ "$OS" == "macOS" ]]; then
        print_info "or install via: brew install git"
    elif [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Kubuntu"* ]]; then
        print_info "or install via: sudo apt install git"
    fi
fi

echo ""
echo "============================================================================"

################################################################################
# 7. Check Maven Build
################################################################################

print_check "7" "7" "Testing Maven build..."

if [[ -f pom.xml ]]; then
    echo "Running: mvn clean compile -q"
    if mvn clean compile -q > /dev/null 2>&1; then
        print_pass "Maven build successful"
    else
        print_fail "Maven build failed"
        print_info "Run manually: mvn clean install"
    fi
else
    print_warn "Not in MCMS project directory (pom.xml not found)"
    print_info "Navigate to project root and run this script again"
fi

echo ""
echo "============================================================================"

################################################################################
# Summary
################################################################################

echo ""
print_header "SUMMARY"
echo ""
echo -e "  ${GREEN}Passed:${NC}  $PASS_COUNT"
echo -e "  ${RED}Failed:${NC}  $FAIL_COUNT"
echo -e "  ${YELLOW}Warnings:${NC} $WARN_COUNT"
echo ""

if [[ $FAIL_COUNT -eq 0 ]]; then
    if [[ $WARN_COUNT -eq 0 ]]; then
        echo -e "${GREEN}✓ ALL CHECKS PASSED${NC}"
        echo ""
        echo "  Your system is ready for MCMS development!"
        echo ""
        echo "  Next steps:"
        echo "    1. Read: dev-docs/AGENT_MEMORIES.md"
        echo "    2. Read: docs/AGENTS.md"
        echo "    3. Build: mvn clean install"
        echo ""
    else
        echo -e "${YELLOW}⚠ CHECKS PASSED WITH WARNINGS${NC}"
        echo ""
        echo "  Your system is mostly ready, but some optional items need attention."
        echo "  Review warnings above and fix if needed."
        echo ""
    fi
else
    echo -e "${RED}✗ CHECKS FAILED${NC}"
    echo ""
    echo "  Please install the missing prerequisites before continuing."
    echo "  See: dev-docs/SETUP_GUIDE.md for detailed instructions"
    echo ""
fi

echo "============================================================================"
echo ""

exit $FAIL_COUNT
