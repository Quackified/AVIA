import subprocess
import time
import os
import sys

os.chdir(r'C:\Users\Quacky\Documents\Coding\Android Studio\Projects\AlgoLens')

task = sys.argv[1] if len(sys.argv) > 1 else ':app:compileDebugKotlin'
log_path = 'build_gradle.log'

print(f'Starting {task}, logging to {log_path}...')

with open(log_path, 'w', encoding='utf-8') as log:
    proc = subprocess.Popen(
        ['gradlew.bat', task, '--no-daemon'],
        stdout=log,
        stderr=subprocess.STDOUT
    )

print(f'Gradle PID: {proc.pid}')
deadline = time.time() + 540

while time.time() < deadline:
    time.sleep(15)
    if proc.poll() is not None:
        break
    try:
        with open(log_path, 'r', encoding='utf-8', errors='replace') as lf:
            content = lf.read()
        if 'BUILD SUCCESSFUL' in content:
            print('BUILD SUCCESSFUL')
            sys.exit(0)
        if 'BUILD FAILED' in content:
            print('BUILD FAILED')
            break
    except:
        pass

rc = proc.returncode
print(f'Gradle exited with code: {rc}')

with open(log_path, 'r', encoding='utf-8', errors='replace') as lf:
    lines = lf.readlines()

print(f'\n=== Last 50 lines of {log_path} ({len(lines)} total) ===')
for line in lines[-50:]:
    print(line.rstrip())

if 'BUILD SUCCESSFUL' in ''.join(lines):
    print(f'\n✅ {task} SUCCESSFUL')
    sys.exit(0)
else:
    print(f'\n❌ {task} FAILED')
    sys.exit(1)