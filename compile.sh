#!/bin/bash

# Proje dizini
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$PROJECT_DIR/src"
OUT_DIR="$PROJECT_DIR/out"
LIB_DIR="$PROJECT_DIR/lib"

# Çıktı klasörünü oluştur (varsa temizle)
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# JavaFX jar dosyalarını classpath'e ekle
JAVAFX_JARS="$LIB_DIR/javafx-base-21.jar:$LIB_DIR/javafx-base-21-mac-aarch64.jar:$LIB_DIR/javafx-controls-21.jar:$LIB_DIR/javafx-controls-21-mac-aarch64.jar:$LIB_DIR/javafx-fxml-21.jar:$LIB_DIR/javafx-fxml-21-mac-aarch64.jar:$LIB_DIR/javafx-graphics-21.jar:$LIB_DIR/javafx-graphics-21-mac-aarch64.jar"

# JUnit jar dosyaları (testler için)
JUNIT_JARS="$LIB_DIR/junit-jupiter-api-5.10.0.jar:$LIB_DIR/junit-jupiter-engine-5.10.0.jar:$LIB_DIR/junit-platform-commons-1.10.0.jar:$LIB_DIR/junit-platform-engine-1.10.0.jar:$LIB_DIR/opentest4j-1.3.0.jar:$LIB_DIR/apiguardian-api-1.1.2.jar"

# Tüm classpath
CLASSPATH="$JAVAFX_JARS:$JUNIT_JARS"

echo "Derleniyor..."

# Tüm .java dosyalarını bul (test klasörü hariç)
find "$SRC_DIR" -name "*.java" ! -path "*/test/*" > "$PROJECT_DIR/sources.txt"

javac --module-path "$LIB_DIR" \
      -d "$OUT_DIR" \
      @"$PROJECT_DIR/sources.txt"

if [ $? -eq 0 ]; then
    echo "✅ Derleme başarılı! Çıktı: $OUT_DIR"
    rm "$PROJECT_DIR/sources.txt"
else
    echo "❌ Derleme hatası!"
    rm "$PROJECT_DIR/sources.txt"
    exit 1
fi
