$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location -Path $projectRoot

# Ensure Maven uses JDK 21. Adjust this path if your JDK is installed elsewhere.
$env:JAVA_HOME = 'C:\Users\JulasJr\.jdk\jdk-21.0.8(1)'
$env:Path = "$env:JAVA_HOME\bin;C:\Users\JulasJr\.maven\maven-3.9.12(1)\bin;$env:Path"

mvn clean javafx:run
