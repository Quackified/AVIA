import subprocess, os, time, sys

os.chdir(r'C:\Users\Quacky\Documents\Coding\Android Studio\Projects\AlgoLens')

# Kill existing processes
for name in ['java.exe', 'javaw.exe']:
    try:
        subprocess.run(['taskkill', '/F', '/IM', name],
                       capture_output=True, text=True, timeout=5)
    except Exception:
        pass

# Clear old log
try:
    os.remove('build_gradle.log')
except Exception:
    pass

# Start Gradle build (background, log to file)
log_f = open('build_gradle.log', 'w', encoding='utf-8')
proc = subprocess.Popen(
    ['gradlew.bat', ':app:compileDebugKotlin', '--no-daemon'],
    stdout=log_f,
    stderr=log_f
)
log_f.close()
print(f'Started Gradle PID={proc.pid}')

# Poll for completion (up to 9 min)
deadline = time.time() + 540
while time.time() < deadline:
    if proc.poll() is not None:
        print(f'Gradle exited with RC={proc.returncode}')
        break
    time.sleep(5)
else:
    print('Timeout expired - killing Gradle')
    proc.kill()
    proc.wait(timeout=30)

# Read and report
with open('build_gradle.log', 'r', encoding='utf-8', errors='replace') as f:
    content = f.read()

lines = content.splitlines()
has_success = 'BUILD SUCCESSFUL' in content
has_failed = 'BUILD FAILED' in content

if has_success:
    status = 'SUCCESS'
elif has_failed:
    status = 'FAILED'
else:
    status = 'UNKNOWN'

print(f'\n=== RESULT: {status} ===')
print(f'Log: {len(lines)} lines, {len(content)} chars')

if status == 'UNKNOWN':
    print('\nLast 15 lines of log:')
    for line in lines[-15:]:
        print(f'  {line[:120]}')
else:
    # Show BUILD line
    for line in lines:
        if 'BUILD' in line:
            print(f'  {line.strip()[:120]}')
    # Show errors if failed
    if has_failed:
        print('\nErrors:')
        for line in lines:
            if 'e:' in line or 'error' in line.lower():
                print(f'  {line.strip()[:150]}')

if has_success:
    sys.exit(0)
else:
    sys.exit(1)
