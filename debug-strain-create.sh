#!/bin/bash

# Get token
echo "Getting auth token..."
AUTH_RESPONSE=$(curl -s -X POST http://localhost:8080/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin","rememberMe":false}')

TOKEN=$(echo "$AUTH_RESPONSE" | grep -o 'eyJ[^"]*')
echo "Token: ${TOKEN:0:30}..."
echo ""

# Try to create strain
echo "Creating strain..."
CREATE_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST http://localhost:8080/api/strains \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "TEST_STRAIN",
    "species": "Test species",
    "active": true
  }')

echo "Response:"
echo "$CREATE_RESPONSE"
