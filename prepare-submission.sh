#!/bin/bash

# ============================================
# Ödev Teslim Paketi Hazırlayıcı - Grup 3
# SADECE hocanın istediği dosyalar:
# - 3.jar
# - src_3.zip (Eclipse uyumlu)
# - lib_3.zip
# - report_3.pdf (sen ekleyeceksin)
# - video_3.mp4 (sen ekleyeceksin)
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
Class-Path: lib_$GROUP_NUMBER/javafx-base-21.jar lib_$GROUP_NUMBER/javafx-controls-21.jar lib_$GROUP_NUMBER/javafx-fxml-21.jar lib_$GROUP_NUMBER/javafx-graphics-21.jar

EOF

cd "$OUT_DIR"
jar cfm "$SUBMISSION_DIR/$GROUP_NUMBER.jar" "$MANIFEST_FILE" .
rm "$MANIFEST_FILE"
echo "✅ $GROUP_NUMBER.jar oluşturuldu!"

# 4. src_3.zip oluştur (Eclipse uyumlu - .project, .classpath, module-info.java dahil)
echo ""
echo "📦 Adım 3: src_$GROUP_NUMBER.zip oluşturuluyor (Eclipse uyumlu)..."
cd "$PROJECT_DIR"
rm -rf "src_$GROUP_NUMBER"
cp -r src "src_$GROUP_NUMBER"

# module-info.java'yı kaldır (classpath yaklaşımı için)
rm -f "src_$GROUP_NUMBER/module-info.java.bak"
rm -f "src_$GROUP_NUMBER/module-info.java"

# Eclipse .project dosyası
cat > "src_$GROUP_NUMBER/.project" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<projectDescription>
	<name>AirlineReservationSystem_Grup$GROUP_NUMBER</name>
	<comment></comment>
	<projects>
	</projects>
	<buildSpec>
		<buildCommand>
			<name>org.eclipse.jdt.core.javabuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
	</buildSpec>
	<natures>
		<nature>org.eclipse.jdt.core.javanature</nature>
	</natures>
</projectDescription>
EOF

# Eclipse .classpath dosyası (tüm JavaFX JAR'ları dahil)
cat > "src_$GROUP_NUMBER/.classpath" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<classpath>
	<classpathentry kind="src" path=""/>
	<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-17"/>
	<classpathentry kind="lib" path="../lib_$GROUP_NUMBER/javafx-base-21.jar"/>
	<classpathentry kind="lib" path="../lib_$GROUP_NUMBER/javafx-base-21-mac-aarch64.jar"/>
	<classpathentry kind="lib" path="../lib_$GROUP_NUMBER/javafx-controls-21.jar"/>
	<classpathentry kind="lib" path="../lib_$GROUP_NUMBER/javafx-controls-21-mac-aarch64.jar"/>
	<classpathentry kind="lib" path="../lib_$GROUP_NUMBER/javafx-fxml-21.jar"/>
	<classpathentry kind="lib" path="../lib_$GROUP_NUMBER/javafx-fxml-21-mac-aarch64.jar"/>
	<classpathentry kind="lib" path="../lib_$GROUP_NUMBER/javafx-graphics-21.jar"/>
	<classpathentry kind="lib" path="../lib_$GROUP_NUMBER/javafx-graphics-21-mac-aarch64.jar"/>
	<classpathentry kind="output" path="bin"/>
</classpath>
EOF

# Eclipse launch dosyası (VM argümanları hazır - çift tıkla çalıştır)
cat > "src_$GROUP_NUMBER/RunAirline.launch" << EOF
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<launchConfiguration type="org.eclipse.jdt.launching.localJavaApplication">
<stringAttribute key="org.eclipse.jdt.launching.MAIN_TYPE" value="com.airline.Launcher"/>
<stringAttribute key="org.eclipse.jdt.launching.PROJECT_ATTR" value="AirlineReservationSystem_Grup$GROUP_NUMBER"/>
<stringAttribute key="org.eclipse.jdt.launching.VM_ARGUMENTS" value="--module-path ../lib_$GROUP_NUMBER --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base"/>
</launchConfiguration>
EOF

zip -rq "$SUBMISSION_DIR/src_$GROUP_NUMBER.zip" "src_$GROUP_NUMBER"
rm -rf "src_$GROUP_NUMBER"
echo "✅ src_$GROUP_NUMBER.zip oluşturuldu!"

# 5. lib_3.zip oluştur
echo ""
echo "📦 Adım 4: lib_$GROUP_NUMBER.zip oluşturuluyor..."
cd "$PROJECT_DIR"
rm -rf "lib_$GROUP_NUMBER"
cp -r lib "lib_$GROUP_NUMBER"
zip -rq "$SUBMISSION_DIR/lib_$GROUP_NUMBER.zip" "lib_$GROUP_NUMBER"
rm -rf "lib_$GROUP_NUMBER"
echo "✅ lib_$GROUP_NUMBER.zip oluşturuldu!"

# 6. Final 3.zip oluştur
echo ""
echo "📦 Adım 5: Final $GROUP_NUMBER.zip oluşturuluyor..."
cd "$PROJECT_DIR/submission"
rm -f "$GROUP_NUMBER.zip"
zip -rq "$GROUP_NUMBER.zip" "$GROUP_NUMBER"
echo "✅ $GROUP_NUMBER.zip oluşturuldu!"

echo ""
echo "=========================================="
echo "✅ TESLİM PAKETİ HAZIRLANDI!"
echo "=========================================="
echo ""
echo "📁 Final dosya: $PROJECT_DIR/submission/$GROUP_NUMBER.zip"
echo ""
echo "📋 ZIP içeriği:"
unzip -l "$PROJECT_DIR/submission/$GROUP_NUMBER.zip"
echo ""
echo "⚠️  EKSİK DOSYALAR (ekleyip scripti tekrar çalıştır):"
echo "   📄 report_$GROUP_NUMBER.pdf"
echo "   🎥 video_$GROUP_NUMBER.mp4"
