$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location -Path $projectRoot

function Resolve-ExistingPath([string[]] $paths) {
	foreach ($p in $paths) {
		if ([string]::IsNullOrWhiteSpace($p)) { continue }
		if (Test-Path -LiteralPath $p) { return $p }
	}
	return $null
}

function Resolve-FirstGlobMatch([string[]] $globs) {
	foreach ($g in $globs) {
		if ([string]::IsNullOrWhiteSpace($g)) { continue }
		$match = Get-ChildItem -Path $g -ErrorAction SilentlyContinue | Select-Object -First 1
		if ($null -ne $match) { return $match.FullName }
	}
	return $null
}

# Prefer existing JAVA_HOME. If not set, try common JDK install locations.
if ([string]::IsNullOrWhiteSpace($env:JAVA_HOME) -or -not (Test-Path -LiteralPath $env:JAVA_HOME)) {
	$jdk = Resolve-FirstGlobMatch @(
		"$env:USERPROFILE\.jdk\jdk-21*",
		"$env:ProgramFiles\Java\jdk-21*",
		"$env:ProgramFiles\Eclipse Adoptium\jdk-21*",
		"$env:ProgramFiles\Microsoft\jdk-21*"
	)
	if ($null -ne $jdk) {
		$env:JAVA_HOME = $jdk
	}
}

if (-not [string]::IsNullOrWhiteSpace($env:JAVA_HOME)) {
	$javaBin = Join-Path $env:JAVA_HOME 'bin'
	if (Test-Path -LiteralPath $javaBin) {
		$env:Path = "$javaBin;$env:Path"
	}
}

# Ensure mvn is available; if not, try a user-local Maven install.
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
	$mavenBin = Resolve-FirstGlobMatch @(
		"$env:USERPROFILE\.maven\maven-*\bin",
		"$env:ProgramFiles\Apache\maven\bin"
	)
	if ($null -ne $mavenBin) {
		$env:Path = "$mavenBin;$env:Path"
	}
}

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
	throw "Maven (mvn) not found. Install Maven or add it to PATH, then re-run this script."
}
if ([string]::IsNullOrWhiteSpace($env:JAVA_HOME) -or -not (Test-Path -LiteralPath $env:JAVA_HOME)) {
	throw "JAVA_HOME is not set to a valid JDK. Install JDK 21 and set JAVA_HOME, then re-run this script."
}

mvn -v
mvn clean javafx:run
