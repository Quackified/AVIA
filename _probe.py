import sqlite3, sys

path = r"C:/Users/Quacky/Documents/Coding/Android Studio/Projects/AlgoLens/cpu-perfetto-20260911T175953.trace"
try:
    con = sqlite3.connect(path)
except Exception as e:
    print("NOT_SQLITE: %r" % e)
    sys.exit(0)

cur = con.cursor()
tables = [r[0] for r in cur.execute("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name")]
print("TABLES(%d): %s" % (len(tables), tables))

# Core perfetto sqlite schema probe
for name in ("events", "threads", "processes", "slices", "tracks", "strings", "metadata", "stats_expensive", "globals"):
    if name in tables:
        cols = [c[1] for c in cur.execute("PRAGMA table_info(%s)" % name)]
        n = cur.execute("SELECT COUNT(*) FROM %s" % name).fetchone()[0]
        print("== %s rows=%d cols=%s" % (name, n, cols))

# If `events` exists (classic perfetto sqlite), print bounds + sample + busy names
if "events" in tables:
    try:
        row = cur.execute("SELECT MIN(ts), MAX(ts), COUNT(*) FROM events").fetchone()
        print("events ts range: min=%s max=%s total=%s" % (row[0], row[1], row[2]))
        for r in cur.execute("SELECT name, COUNT(*) c, MIN(ts), MAX(ts) FROM events GROUP BY name ORDER BY c DESC LIMIT 40"):
            print("EVENT %-40s count=%-8s ts[%s .. %s]" % (r[0], r[1], r[2], r[3]))
    except Exception as e:
        print("events query err %r" % e)

# slices (span tracks) if present
if "slices" in tables:
    try:
        for r in cur.execute("SELECT name, COUNT(*) c, MIN(start_ts), MAX(start_ts) FROM slices GROUP BY name ORDER BY c DESC LIMIT 40"):
            print("SLICE %-40s count=%-8s start[%s .. %s]" % (r[0], r[1], r[2], r[3]))
    except Exception as e:
        print("slices query err %r" % e)

con.close()