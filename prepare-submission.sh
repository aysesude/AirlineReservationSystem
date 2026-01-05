#!/bin/bash

# ============================================
# Ödev Teslim Paketi Hazırlayıcı - Grup 3
# ============================================

GROUP_NUMBER="3"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$PROJECT_DIR/src"
LIB_DIR="$PROJECT_DIR/lib"
OUT_DIR="$PROJECT_DIR/out"
SUBMISSION_DIR="$PROJECT_DIR/submission/$GROUP_NUMBER"

echo "=========================================="
echo "  Grup $GROUP_NUMBER - Teslim Paketi Hazırlanıyor"
echo "=========================================="

# 1. Submission klasörünü oluştur
rm -rf "$PROJECT_DIR/submission"
mkdir -p "$SUBMISSION_DIR"

# 2. Kaynak kodları derle
echo ""
echo "📦 Adım 1: Kaynak kodlar derleniyor..."
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# Platform-bağımsız JAR'ları ve bir native JAR setini kullan
JAVAFX_MODULE_PATH="$LIB_DIR/javafx-base-21.jar:$LIB_DIR/javafx-base-21-mac-aarch64.jar:$LIB_DIR/javafx-controls-21.jar:$LIB_DIR/javafx-controls-21-mac-aarch64.jar:$LIB_DIR/javafx-fxml-21.jar:$LIB_DIR/javafx-fxml-21-mac-aarch64.jar:$LIB_DIR/javafx-graphics-21.jar:$LIB_DIR/javafx-graphics-21-mac-aarch64.jar"

find "$SRC_DIR" -name "*.java" ! -path "*/test/*" > "$PROJECT_DIR/sources.txt"

javac --module-path "$JAVAFX_MODULE_PATH" \
      --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
      --release 17 \
      -d "$OUT_DIR" \
      @"$PROJECT_DIR/sources.txt"

if [ $? -ne 0 ]; then
    echo "❌ Derleme hatası!"
    rm "$PROJECT_DIR/sources.txt"
    exit 1
fi
rm "$PROJECT_DIR/sources.txt"
echo "✅ Derleme başarılı!"

# 3. JAR dosyası oluştur (3.jar)
echo ""
echo "📦 Adım 2: $GROUP_NUMBER.jar oluşturuluyor..."

MANIFEST_FILE="$PROJECT_DIR/MANIFEST.MF"
cat > "$MANIFEST_FILE" << EOF
Manifest-Version: 1.0
Main-Class: com.airline.Launcher
Class-Path: lib_$GROUP_NUMBER/javafx-base-21.jar lib_$GROUP_NUMBER/javafx-base-21-mac-aarch64.jar lib_$GROUP_NUMBER/javafx-controls-21.jar lib_$GROUP_NUMBER/javafx-controls-21-mac-aarch64.jar lib_$GROUP_NUMBER/javafx-fxml-21.jar lib_$GROUP_NUMBER/javafx-fxml-21-mac-aarch64.jar lib_$GROUP_NUMBER/javafx-graphics-21.jar lib_$GROUP_NUMBER/javafx-graphics-21-mac-aarch64.jar

EOF

cd "$OUT_DIR"
jar cfm "$SUBMISSION_DIR/$GROUP_NUMBER.jar" "$MANIFEST_FILE" .
rm "$MANIFEST_FILE"
echo "✅ $GROUP_NUMBER.jar oluşturuldu!"

# 4. src_3.zip oluştur
echo ""
echo "📦 Adım 3: src_$GROUP_NUMBER.zip oluşturuluyor..."
cd "$PROJECT_DIR"
# src klasörünü src_3 olarak kopyala ve zipele
rm -rf "src_$GROUP_NUMBER"
cp -r src "src_$GROUP_NUMBER"
zip -rq "$SUBMISSION_DIR/src_$GROUP_NUMBER.zip" "src_$GROUP_NUMBER"
rm -rf "src_$GROUP_NUMBER"
echo "✅ src_$GROUP_NUMBER.zip oluşturuldu!"

# 5. lib_3.zip oluştur (JavaFX kütüphaneleri)
echo ""
echo "📦 Adım 4: lib_$GROUP_NUMBER.zip oluşturuluyor..."
cd "$PROJECT_DIR"
rm -rf "lib_$GROUP_NUMBER"
cp -r lib "lib_$GROUP_NUMBER"
zip -rq "$SUBMISSION_DIR/lib_$GROUP_NUMBER.zip" "lib_$GROUP_NUMBER"
rm -rf "lib_$GROUP_NUMBER"
echo "✅ lib_$GROUP_NUMBER.zip oluşturuldu!"

# 6. Çalıştırma talimatları oluştur
echo ""
echo "📦 Adım 5: Çalıştırma talimatları oluşturuluyor..."
cat > "$SUBMISSION_DIR/CALISTIRMA_TALIMATLARI.txt" << EOF
========================================
  Havayolu Rezervasyon Sistemi - Grup 3
  Çalıştırma Talimatları
========================================

ÖNEMLİ: Bu proje JavaFX kullandığı için JAR dosyası lib klasörüyle birlikte çalıştırılmalıdır.

ADIMLAR:
1. lib_3.zip dosyasını JAR dosyasıyla aynı klasöre çıkarın
2. Aşağıdaki komutu çalıştırın:

macOS/Linux:
-----------
java --module-path lib_3 --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -jar 3.jar

Windows:
--------
java --module-path lib_3 --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -jar 3.jar

NOT: Java 17 veya üzeri sürüm gereklidir.
EOF
echo "✅ Çalıştırma talimatları oluşturuldu!"

# 7. Çalıştırma script'i oluştur
cat > "$SUBMISSION_DIR/run.sh" << 'EOF'
#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# lib_3.zip'i çıkar (eğer çıkarılmamışsa)
if [ ! -d "$SCRIPT_DIR/lib_3" ]; then
    if [ -f "$SCRIPT_DIR/lib_3.zip" ]; then
        unzip -q "$SCRIPT_DIR/lib_3.zip" -d "$SCRIPT_DIR"
    else
        echo "❌ lib_3.zip bulunamadı!"
        exit 1
    fi
fi

java --module-path "$SCRIPT_DIR/lib_3" \
     --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
     --enable-native-access=javafx.graphics \
     -jar "$SCRIPT_DIR/3.jar" 2>&1 | grep -v "^WARNING:"
EOF
chmod +x "$SUBMISSION_DIR/run.sh"

cat > "$SUBMISSION_DIR/run.bat" << 'EOF'
@echo off
cd /d "%~dp0"

if not exist "lib_3" (
    if exist "lib_3.zip" (
        powershell -command "Expand-Archive -Path 'lib_3.zip' -DestinationPath '.'"
    ) else (
        echo lib_3.zip bulunamadi!
        pause
        exit /b 1
    )
)

java --module-path lib_3 --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -jar 3.jar
pause
EOF

echo ""
echo "=========================================="
echo "✅ TESLİM PAKETİ HAZIRLANDI!"
echo "=========================================="
echo ""
echo "📁 Klasör: $SUBMISSION_DIR"
echo ""
echo "📋 Oluşturulan dosyalar:"
ls -la "$SUBMISSION_DIR"
echo ""
echo "⚠️  EKSİK DOSYALAR (sizin eklemeniz gereken):"
echo "   📄 report_$GROUP_NUMBER.pdf  - UML ve açıklamaları"
echo "   🎥 video_$GROUP_NUMBER.mp4   - Tanıtım videosu"
echo ""
echo "📌 SONRAKİ ADIMLAR:"
echo "   1. report_$GROUP_NUMBER.pdf dosyasını $SUBMISSION_DIR klasörüne ekleyin"
echo "   2. video_$GROUP_NUMBER.mp4 dosyasını $SUBMISSION_DIR klasörüne ekleyin"
echo "   3. Aşağıdaki komutu çalıştırarak son zip'i oluşturun:"
echo ""
echo "      cd $PROJECT_DIR/submission && zip -r $GROUP_NUMBER.zip $GROUP_NUMBER"
echo ""
