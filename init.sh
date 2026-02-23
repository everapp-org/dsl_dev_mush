#!/bin/bash

# Mushroom Cultivation Management System (MCMS)
# Initialization and startup script

set -e  # Exit on error

echo "==================================="
echo "MCMS - Mushroom Cultivation System"
echo "==================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
print_info "Checking prerequisites..."

# Check Java
if ! command -v java &> /dev/null; then
    print_error "Java not found. Please install Java 21."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    print_warn "Java version is $JAVA_VERSION. Java 21 is recommended."
fi
print_info "Java version: $(java -version 2>&1 | head -n 1)"

# Check Maven
if ! command -v mvn &> /dev/null; then
    print_error "Maven not found. Please install Maven 3.9+."
    exit 1
fi
print_info "Maven version: $(mvn -version | head -n 1)"

# Check Node.js
if ! command -v node &> /dev/null; then
    print_error "Node.js not found. Please install Node.js 18+."
    exit 1
fi
print_info "Node.js version: $(node -v)"

# Check npm
if ! command -v npm &> /dev/null; then
    print_error "npm not found. Please install npm."
    exit 1
fi
print_info "npm version: $(npm -v)"

echo ""
print_info "All prerequisites satisfied!"
echo ""

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
    print_info "Installing frontend dependencies..."
    npm install
fi

if [ ! -d "target" ]; then
    print_info "Building backend (first time)..."
    ./mvnw clean install -DskipTests
else
    print_info "Backend already built. To rebuild, run: ./mvnw clean install"
fi

# Check if ANTLR grammars need to be compiled
if [ -d "mcms-dsl/src/main/antlr4" ]; then
    print_info "Compiling ANTLR grammars..."
    cd mcms-dsl
    ../mvnw antlr4:antlr4
    cd ..
fi

# Check if DSL code generation needs to run
if [ -d "mcms-codegen" ]; then
    print_info "Running DSL code generation..."
    cd mcms-codegen
    ../mvnw exec:java
    cd ..
fi

echo ""
print_info "Starting development servers..."
echo ""

# Kill any existing processes on ports 8080 and 9000
print_info "Checking for existing processes on ports 8080 and 9000..."
lsof -ti :8080 | xargs kill -9 2>/dev/null || true
lsof -ti :9000 | xargs kill -9 2>/dev/null || true
sleep 2

# Start backend (Spring Boot)
print_info "Starting Spring Boot backend on port 8080..."
./mvnw spring-boot:run &
BACKEND_PID=$!

# Wait for backend to start
print_info "Waiting for backend to start..."
for i in {1..60}; do
    if curl -s http://localhost:8080/management/health > /dev/null 2>&1; then
        print_info "Backend is up and running!"
        break
    fi
    if [ $i -eq 60 ]; then
        print_error "Backend failed to start within 60 seconds."
        kill $BACKEND_PID 2>/dev/null || true
        exit 1
    fi
    sleep 1
done

# Start frontend (webpack dev server for React)
print_info "Starting frontend development server on port 9000..."
npm start &
FRONTEND_PID=$!

# Wait for frontend to start
print_info "Waiting for frontend to start..."
for i in {1..30}; do
    if curl -s http://localhost:9000 > /dev/null 2>&1; then
        print_info "Frontend is up and running!"
        break
    fi
    if [ $i -eq 30 ]; then
        print_warn "Frontend may still be starting..."
    fi
    sleep 1
done

echo ""
echo "==================================="
print_info "MCMS is now running!"
echo "==================================="
echo ""
echo "  Frontend:  http://localhost:9000"
echo "  Backend:   http://localhost:8080"
echo "  API Docs:  http://localhost:8080/swagger-ui/"
echo "  H2 Console: http://localhost:8080/h2-console (if using H2)"
echo ""
echo "  Default admin credentials:"
echo "    Username: admin"
echo "    Password: admin"
echo ""
print_info "Press Ctrl+C to stop both servers"
echo ""

# Wait for Ctrl+C
trap "echo ''; print_info 'Stopping servers...'; kill $BACKEND_PID $FRONTEND_PID 2>/dev/null || true; exit 0" INT

# Keep script running
wait
