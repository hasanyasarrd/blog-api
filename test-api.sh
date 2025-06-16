#!/bin/bash

API_BASE="http://localhost:8080"
TEST_USER="testuser_$(date +%s)"
TEST_EMAIL="test_$(date +%s)@example.com"
TEST_PASSWORD="password123"

echo "🧪 Blog API Test Başlıyor..."

# Test 1: Kullanıcı Kaydı
echo "📝 Test 1: Kullanıcı Kaydı"
REGISTER_RESPONSE=$(curl -s -w "%{http_code}" -X POST "$API_BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$TEST_USER\",
    \"email\": \"$TEST_EMAIL\",
    \"password\": \"$TEST_PASSWORD\"
  }")

HTTP_CODE="${REGISTER_RESPONSE: -3}"
if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ Kullanıcı kaydı başarılı"
else
    echo "❌ Kullanıcı kaydı başarısız (HTTP: $HTTP_CODE)"
    echo "Response: ${REGISTER_RESPONSE%???}"
    exit 1
fi

# Test 2: Giriş ve Token Alma
echo "🔐 Test 2: Kullanıcı Girişi"
LOGIN_RESPONSE=$(curl -s -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$TEST_USER\",
    \"password\": \"$TEST_PASSWORD\"
  }")

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
if [ "$TOKEN" != "" ]; then
    echo "✅ Giriş başarılı, token alındı"
else
    echo "❌ Giriş başarısız"
    echo "Response: $LOGIN_RESPONSE"
    exit 1
fi

# Test 3: Post Oluşturma
echo "📄 Test 3: Post Oluşturma"
POST_RESPONSE=$(curl -s -w "%{http_code}" -X POST "$API_BASE/posts" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Blog Yazısı",
    "content": "Bu bir test yazısıdır. API test edilmektedir."
  }')

HTTP_CODE="${POST_RESPONSE: -3}"
if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ Post oluşturma başarılı"
    POST_ID=$(echo "${POST_RESPONSE%???}" | grep -o '"id":[0-9]*' | cut -d':' -f2)
else
    echo "❌ Post oluşturma başarısız (HTTP: $HTTP_CODE)"
    echo "Response: ${POST_RESPONSE%???}"
    exit 1
fi

# Test 4: Postları Listeleme
echo "📋 Test 4: Postları Listeleme"
LIST_RESPONSE=$(curl -s -w "%{http_code}" -X GET "$API_BASE/posts")
HTTP_CODE="${LIST_RESPONSE: -3}"
if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ Postları listeleme başarılı"
else
    echo "❌ Postları listeleme başarısız (HTTP: $HTTP_CODE)"
fi

# Test 5: Yorum Ekleme
echo "💬 Test 5: Yorum Ekleme"
COMMENT_RESPONSE=$(curl -s -w "%{http_code}" -X POST "$API_BASE/posts/$POST_ID/comments" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Bu bir test yorumudur."
  }')

HTTP_CODE="${COMMENT_RESPONSE: -3}"
if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ Yorum ekleme başarılı"
else
    echo "❌ Yorum ekleme başarısız (HTTP: $HTTP_CODE)"
fi

echo ""
echo "🎉 Tüm testler tamamlandı!"
echo "📊 Test Sonuçları:"
echo "   - Kullanıcı: $TEST_USER"
echo "   - Post ID: $POST_ID"
echo "   - Token: ${TOKEN:0:20}..."
