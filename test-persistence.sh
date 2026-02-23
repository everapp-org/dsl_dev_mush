#!/bin/bash
set -e

echo "=== MCMS Data Persistence Test (Feature #3) ==="
echo ""

# Step 1: Authenticate and get token
echo "Step 1: Authenticating as admin..."
AUTH_RESPONSE=$(curl -s -X POST http://localhost:8080/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin","rememberMe":false}')

TOKEN=$(echo "$AUTH_RESPONSE" | grep -o 'eyJ[^"]*')

if [ -z "$TOKEN" ]; then
  echo "ERROR: Failed to get authentication token"
  echo "Response: $AUTH_RESPONSE"
  exit 1
fi

echo "✓ Authentication successful"
echo ""

# Step 2: Create unique test strain
echo "Step 2: Creating test strain RESTART_TEST_12345..."
CREATE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/strains \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "RESTART_TEST_12345",
    "species": "Pleurotus ostreatus",
    "active": true
  }')

STRAIN_ID=$(echo "$CREATE_RESPONSE" | grep -o '"id"[[:space:]]*:[[:space:]]*[0-9]*' | grep -o '[0-9]*')

if [ -z "$STRAIN_ID" ]; then
  echo "ERROR: Failed to create strain"
  echo "Response: $CREATE_RESPONSE"
  exit 1
fi

echo "✓ Strain created with ID: $STRAIN_ID"
echo ""

# Step 3: Verify strain exists
echo "Step 3: Verifying strain exists before restart..."
GET_RESPONSE=$(curl -s -X GET http://localhost:8080/api/strains/$STRAIN_ID \
  -H "Authorization: Bearer $TOKEN")

if echo "$GET_RESPONSE" | grep -q "RESTART_TEST_12345"; then
  echo "✓ Strain verified in database"
else
  echo "ERROR: Strain not found before restart"
  echo "Response: $GET_RESPONSE"
  exit 1
fi
echo ""

# Step 4: Get backend PID
echo "Step 4: Finding backend server process..."
BACKEND_PID=$(lsof -ti :8080 2>/dev/null || true)

if [ -z "$BACKEND_PID" ]; then
  echo "ERROR: Backend server not running on port 8080"
  exit 1
fi

echo "✓ Backend running on PID: $BACKEND_PID"
echo ""

# Step 5: Stop server
echo "Step 5: Stopping backend server..."
kill -9 $BACKEND_PID 2>/dev/null || true
sleep 5

# Verify server stopped
SERVER_CHECK=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/strains 2>/dev/null || echo "000")
if [ "$SERVER_CHECK" != "000" ]; then
  echo "WARNING: Server still responding, waiting longer..."
  sleep 5
fi

echo "✓ Backend server stopped"
echo ""

# Step 6: Restart server
echo "Step 6: Restarting backend server..."
cd /home/gpetrov/src/dsl_dev_mush/mcms-jhipster-base
nohup ../mcms-jhipster-base/mvnw spring-boot:run -Dspring-boot.run.profiles=dev > ../backend-restart.log 2>&1 &
RESTART_PID=$!

echo "✓ Backend restart initiated (PID: $RESTART_PID)"
echo "Waiting for server to be ready (60 seconds)..."

# Wait for server to be ready
READY=0
for i in {1..60}; do
  sleep 1
  HEALTH_CHECK=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/management/health 2>/dev/null || echo "000")
  if [ "$HEALTH_CHECK" = "200" ]; then
    READY=1
    echo "✓ Server ready after $i seconds"
    break
  fi
  if [ $((i % 10)) -eq 0 ]; then
    echo "  Still waiting... ($i seconds elapsed)"
  fi
done

if [ $READY -eq 0 ]; then
  echo "ERROR: Server did not become ready within 60 seconds"
  echo "Last few log lines:"
  tail -20 ../backend-restart.log
  exit 1
fi

echo ""

# Step 7: Re-authenticate (token expired after restart)
echo "Step 7: Re-authenticating after restart..."
sleep 2
AUTH_RESPONSE2=$(curl -s -X POST http://localhost:8080/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin","rememberMe":false}')

TOKEN2=$(echo "$AUTH_RESPONSE2" | grep -o 'eyJ[^"]*')

if [ -z "$TOKEN2" ]; then
  echo "ERROR: Failed to re-authenticate"
  echo "Response: $AUTH_RESPONSE2"
  exit 1
fi

echo "✓ Re-authentication successful"
echo ""

# Step 8: Check if data persisted
echo "Step 8: Verifying RESTART_TEST_12345 still exists..."
GET_RESPONSE2=$(curl -s -X GET http://localhost:8080/api/strains/$STRAIN_ID \
  -H "Authorization: Bearer $TOKEN2")

if echo "$GET_RESPONSE2" | grep -q "RESTART_TEST_12345"; then
  echo "✓✓✓ SUCCESS: Data persisted across server restart!"
  echo "Strain data: $GET_RESPONSE2"
else
  echo "❌❌❌ CRITICAL FAILURE: Data was LOST after restart (in-memory storage detected)"
  echo "Response: $GET_RESPONSE2"
  echo ""
  echo "This indicates the application is using in-memory storage instead of a persistent database."
  exit 1
fi
echo ""

# Step 9: Cleanup - delete test strain
echo "Step 9: Cleaning up test data..."
DELETE_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE http://localhost:8080/api/strains/$STRAIN_ID \
  -H "Authorization: Bearer $TOKEN2")

if [ "$DELETE_RESPONSE" = "204" ] || [ "$DELETE_RESPONSE" = "200" ]; then
  echo "✓ Test strain deleted successfully"
else
  echo "WARNING: Delete returned HTTP $DELETE_RESPONSE (strain may still exist)"
fi

echo ""
echo "=== PERSISTENCE TEST PASSED ==="
echo "Data successfully survived full server restart."
echo "Database is using file-based persistence, not in-memory storage."
