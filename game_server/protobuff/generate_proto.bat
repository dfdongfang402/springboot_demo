@echo off

cd /d %~dp0
for %%s in (proto/*.proto) do (
    if not "%%s"=="." (
		protoc_3_6.exe --java_out=../src/main/java/ --proto_path=proto proto/%%s
	)
)

echo "generator succ!"
pause