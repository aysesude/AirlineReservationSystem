#!/bin/bash

# ============================================
# Airline Reservation System - JAR Builder
# ============================================

# Proje dizini
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$PROJECT_DIR/src"
OUT_DIR="$PROJECT_DIR/out"
LIB_DIR="$PROJECT_DIR/lib"
JAR_DIR="$PROJECT_DIR/dist"
JAR_NAME="AirlineReservation.jar"

echo "=========================================="
echo "  Airline Reservation System JAR Builder"
echo "=========================================="

# 1. Önce derleme yap
echo ""
echo "📦 Adım 1: Kaynak kodlar derleniyor..."
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# Tüm .java dosyalarını bul (test klasörü hariç)
find "$SRC_DIR" -name "*.java" ! -path "*/test/*" > "$PROJECT_DIR/sources.txt"

javac --module-path "$LIB_DIR" \
      --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
      -d "$OUT_DIR" \
      @"$PROJECT_DIR/sources.txt"

if [ $? -ne 0 ]; then
    echo "❌ Derleme hatası!"
    rm "$PROJECT_DIR/sources.txt"
    exit 1
fi
rm "$PROJECT_DIR/sources.txt"
echo "✅ Derleme başarılı!"

# 2. dist klasörünü oluştur
echo ""
echo "📦 Adım 2: JAR dosyası oluşturuluyor..."
rm -rf "$JAR_DIR"
mkdir -p "$JAR_DIR"

# 3. MANIFEST dosyası oluştur
MANIFEST_FILE="$JAR_DIR/MANIFEST.MF"
cat > "$MANIFEST_FILE" << EOF
Manifest-Version: 1.0
Main-Class: com.airline.Launcher
Class-Path: lib/javafx-base-21.jar lib/javafx-base-21-mac-aarch64.jar lib/javafx-controls-21.jar lib/javafx-controls-21-mac-aarch64.jar lib/javafx-fxml-21.jar lib/javafx-fxml-21-mac-aarch64.jar lib/javafx-graphics-21.jar lib/javafx-graphics-21-mac-aarch64.jar

EOF

# 4. JAR dosyası oluştur
cd "$OUT_DIR"
jar cfm "$JAR_DIR/$JAR_NAME" "$MANIFEST_FILE" .

if [ $? -eq 0 ]; then
    echo "✅ JAR dosyası oluşturuldu: $JAR_DIR/$JAR_NAME"
else
    echo "❌ JAR oluşturma hatası!"
    exit 1
fi

# 5. lib klasörünü dist içine kopyala (JavaFX jar'ları için)
echo ""
echo "📦 Adım 3: Bağımlılıklar kopyalanıyor..."
cp -r "$LIB_DIR" "$JAR_DIR/"
echo "✅ Bağımlılıklar kopyalandı!"

# 6. data klasörünü kopyala (eğer varsa)
if [ -d "$PROJECT_DIR/data" ]; then
    cp -r "$PROJECT_DIR/data" "$JAR_DIR/"
    echo "✅ Data klasörü kopyalandı!"
fi

# 7. Çalıştırma scripti oluştur (macOS için)
cat > "$JAR_DIR/run-mac.sh" << 'EOF'
#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
java --module-path "$SCRIPT_DIR/lib" \
     --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
     --enable-native-access=javafx.graphics \
     -jar "$SCRIPT_DIR/AirlineReservation.jar" 2>&1 | grep -v "^WARNING:"
EOF
chmod +x "$JAR_DIR/run-mac.sh"

# 8. Çalıştırma scripti oluştur (Windows için)
cat > "$JAR_DIR/run-windows.bat" << 'EOF'
@echo off
set SCRIPT_DIR=%~dp0
java --module-path "%SCRIPT_DIR%lib" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base ^
     --enable-native-access=javafx.graphics ^
     -jar "%SCRIPT_DIR%AirlineReservation.jar"
pause
EOF

# 9. Linux için çalıştırma scripti
cat > "$JAR_DIR/run-linux.sh" << 'EOF'
#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
java --module-path "$SCRIPT_DIR/lib" \
     --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
     -jar "$SCRIPT_DIR/AirlineReservation.jar"
EOF
chmod +x "$JAR_DIR/run-linux.sh"

echo ""
echo "=========================================="
echo "✅ JAR OLUŞTURMA TAMAMLANDI!"
echo "=========================================="
echo ""
echo "📁 Çıktı klasörü: $JAR_DIR"
echo ""
echo "📋 Oluşturulan dosyalar:"
ls -la "$JAR_DIR"
echo ""
echo "🚀 Uygulamayı çalıştırmak için:"
echo "   macOS:   cd dist && ./run-mac.sh"
echo "   Windows: cd dist && run-windows.bat"
echo "   Linux:   cd dist && ./run-linux.sh"
echo ""
