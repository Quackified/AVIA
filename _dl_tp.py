import os, urllib.request, ssl, sys

SKILL_ROOT = r"C:/Users/Quacky/Documents/Coding/Android Studio/Projects/AlgoLens/.agents/skills/profilers/android-profiler"
url = "https://get.perfetto.dev/trace_processor"
dest_dir = os.path.join(SKILL_ROOT, "bin")
os.makedirs(dest_dir, exist_ok=True)
dest = os.path.join(dest_dir, "trace_processor")

try:
    ctx = ssl.create_default_context()
    req = urllib.request.Request(url, headers={"User-Agent": "perfetto-trace-processor/1"})
    with urllib.request.urlopen(req, timeout=30, context=ctx) as r:
        data = r.read()
    with open(dest, "wb") as f:
        f.write(data)
    print("DOWNLOADED size=%d to %s" % (len(data), dest))
    print("HEAD:", data[:80])
except Exception as e:
    print("DOWNLOAD_FAILED: %r" % e)
    sys.exit(1)