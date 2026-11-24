#!/usr/bin/env sh
# Bootstrap-Skript: lädt Gradle 9.1.0 bei Bedarf herunter und startet es.
set -eu
VERSION="9.1.0"
BASE="${GRADLE_USER_HOME:-$HOME/.gradle}/wrapper/dists/gradle-${VERSION}-bin/local"
GRADLE="$BASE/gradle-${VERSION}/bin/gradle"
if [ ! -x "$GRADLE" ]; then
  mkdir -p "$BASE"
  ZIP="$BASE/gradle.zip"
  if command -v curl >/dev/null 2>&1; then
    curl -fL "https://services.gradle.org/distributions/gradle-${VERSION}-bin.zip" -o "$ZIP"
  elif command -v wget >/dev/null 2>&1; then
    wget "https://services.gradle.org/distributions/gradle-${VERSION}-bin.zip" -O "$ZIP"
  else
    echo "curl oder wget wird benötigt, um Gradle herunterzuladen." >&2; exit 1
  fi
  if command -v unzip >/dev/null 2>&1; then unzip -q "$ZIP" -d "$BASE"
  else echo "unzip wird benötigt, um Gradle zu entpacken." >&2; exit 1; fi
  rm -f "$ZIP"
fi
exec "$GRADLE" "$@"
