import pandas as pd
import matplotlib.pyplot as plt

# === Manually define data ===
# Data for λ=128
data_128 = [
    {"Block size":256,"Avg time per block (ms)":0.064,"CSP proof gen time (ms)":0.0521,"TPA verification time (ms)":0.022},
    {"Block size":512,"Avg time per block (ms)":0.0903,"CSP proof gen time (ms)":0.101,"TPA verification time (ms)":0.0315},
    {"Block size":1024,"Avg time per block (ms)":0.1787,"CSP proof gen time (ms)":0.1669,"TPA verification time (ms)":0.0337},
    {"Block size":2024,"Avg time per block (ms)":0.2396,"CSP proof gen time (ms)":0.1394,"TPA verification time (ms)":0.0125},
]
data_128_enh = [
    {"Block size":256,"Avg time per block (ms)":0.1194,"CSP proof gen time (ms)":0.0677,"TPA verification time (ms)":0.0311},
    {"Block size":512,"Avg time per block (ms)":0.1894,"CSP proof gen time (ms)":0.1134,"TPA verification time (ms)":0.03},
    {"Block size":1024,"Avg time per block (ms)":0.4042,"CSP proof gen time (ms)":0.1764,"TPA verification time (ms)":0.0212},
    {"Block size":2024,"Avg time per block (ms)":0.7863,"CSP proof gen time (ms)":0.2196,"TPA verification time (ms)":0.0179},
]
data_128_alg = [
    {"Block size":256,"Avg time per block (ms)":0.7323,"CSP proof gen time (ms)":0.2429,"TPA verification time (ms)":0.2928},
    {"Block size":512,"Avg time per block (ms)":1.4625,"CSP proof gen time (ms)":0.5018,"TPA verification time (ms)":0.5444},
    {"Block size":1024,"Avg time per block (ms)":2.7993,"CSP proof gen time (ms)":1.3196,"TPA verification time (ms)":1.3242},
    {"Block size":2024,"Avg time per block (ms)":5.3375,"CSP proof gen time (ms)":3.9144,"TPA verification time (ms)":3.4402},
]

# λ=192
data_192 = [
    {"Block size":256,"Avg time per block (ms)":0.0606,"CSP proof gen time (ms)":0.058,"TPA verification time (ms)":0.0145},
    {"Block size":512,"Avg time per block (ms)":0.1585,"CSP proof gen time (ms)":0.9922,"TPA verification time (ms)":0.0215},
    {"Block size":1024,"Avg time per block (ms)":0.1221,"CSP proof gen time (ms)":0.1064,"TPA verification time (ms)":0.0294},
    {"Block size":2048,"Avg time per block (ms)":0.2031,"CSP proof gen time (ms)":0.1404,"TPA verification time (ms)":0.0101},
]
data_192_enh = [
    {"Block size":256,"Avg time per block (ms)":0.1322,"CSP proof gen time (ms)":0.0799,"TPA verification time (ms)":0.055},
    {"Block size":512,"Avg time per block (ms)":0.2644,"CSP proof gen time (ms)":0.1516,"TPA verification time (ms)":0.0513},
    {"Block size":1024,"Avg time per block (ms)":0.3582,"CSP proof gen time (ms)":0.3038,"TPA verification time (ms)":0.0216},
    {"Block size":2048,"Avg time per block (ms)":0.6289,"CSP proof gen time (ms)":0.2395,"TPA verification time (ms)":0.0154},
]
data_192_alg = [
    {"Block size":256,"Avg time per block (ms)":0.8436,"CSP proof gen time (ms)":0.7618,"TPA verification time (ms)":0.5444},
    {"Block size":512,"Avg time per block (ms)":2.1087,"CSP proof gen time (ms)":1.8151,"TPA verification time (ms)":1.2903},
    {"Block size":1024,"Avg time per block (ms)":4.6367,"CSP proof gen time (ms)":1.9906,"TPA verification time (ms)":2.061},
    {"Block size":2048,"Avg time per block (ms)":6.4946,"CSP proof gen time (ms)":4.7389,"TPA verification time (ms)":4.2524},
]

# λ=256 (dummy values just for demonstration)
data_256 = [
    {"Block size":256,"Avg time per block (ms)":0.0788,"CSP proof gen time (ms)":0.0592,"TPA verification time (ms)":0.0366},
    {"Block size":512,"Avg time per block (ms)":0.165,"CSP proof gen time (ms)":0.2056,"TPA verification time (ms)":0.0486},
    {"Block size":1024,"Avg time per block (ms)":0.1432,"CSP proof gen time (ms)":0.1004,"TPA verification time (ms)":0.0296},
    {"Block size":2048,"Avg time per block (ms)":0.195,"CSP proof gen time (ms)":0.1334,"TPA verification time (ms)":0.0148},
]
data_256_enh = [
    {"Block size":256,"Avg time per block (ms)":0.1428,"CSP proof gen time (ms)":0.0687,"TPA verification time (ms)":0.038},
    {"Block size":512,"Avg time per block (ms)":0.3575,"CSP proof gen time (ms)":0.344,"TPA verification time (ms)":0.0388},
    {"Block size":1024,"Avg time per block (ms)":0.3641,"CSP proof gen time (ms)":0.2975,"TPA verification time (ms)":0.019},
    {"Block size":2048,"Avg time per block (ms)":0.6453,"CSP proof gen time (ms)":0.2494,"TPA verification time (ms)":0.0152},
]
data_256_alg = [
    {"Block size":256,"Avg time per block (ms)":1.0106,"CSP proof gen time (ms)":0.6274,"TPA verification time (ms)":0.5802},
    {"Block size":512,"Avg time per block (ms)":2.1524,"CSP proof gen time (ms)":1.9454,"TPA verification time (ms)":1.1618},
    {"Block size":1024,"Avg time per block (ms)":3.857,"CSP proof gen time (ms)":2.2427,"TPA verification time (ms)":2.2458},
    {"Block size":2048,"Avg time per block (ms)":7.347,"CSP proof gen time (ms)":6.0633,"TPA verification time (ms)":5.3725},
]

# === Compute averages ===
def avg(lst, key): return sum(d[key] for d in lst)/len(lst)

lambdas = [128,192,256]
avg_basic = [avg(data_128,'Avg time per block (ms)'), avg(data_192,'Avg time per block (ms)'), avg(data_256,'Avg time per block (ms)')]
avg_enh = [avg(data_128_enh,'Avg time per block (ms)'), avg(data_192_enh,'Avg time per block (ms)'), avg(data_256_enh,'Avg time per block (ms)')]
avg_alg = [avg(data_128_alg,'Avg time per block (ms)'), avg(data_192_alg,'Avg time per block (ms)'), avg(data_256_alg,'Avg time per block (ms)')]

csp_basic = [avg(data_128,'CSP proof gen time (ms)'), avg(data_192,'CSP proof gen time (ms)'), avg(data_256,'CSP proof gen time (ms)')]
csp_enh = [avg(data_128_enh,'CSP proof gen time (ms)'), avg(data_192_enh,'CSP proof gen time (ms)'), avg(data_256_enh,'CSP proof gen time (ms)')]
csp_alg = [avg(data_128_alg,'CSP proof gen time (ms)'), avg(data_192_alg,'CSP proof gen time (ms)'), avg(data_256_alg,'CSP proof gen time (ms)')]

tpa_basic = [avg(data_128,'TPA verification time (ms)'), avg(data_192,'TPA verification time (ms)'), avg(data_256,'TPA verification time (ms)')]
tpa_enh = [avg(data_128_enh,'TPA verification time (ms)'), avg(data_192_enh,'TPA verification time (ms)'), avg(data_256_enh,'TPA verification time (ms)')]
tpa_alg = [avg(data_128_alg,'TPA verification time (ms)'), avg(data_192_alg,'TPA verification time (ms)'), avg(data_256_alg,'TPA verification time (ms)')]

# === Plot average tag generation time ===
plt.figure(figsize=(8,5))
plt.plot(lambdas, avg_basic, marker='o', label='Basic Schnorr')
plt.plot(lambdas, avg_enh, marker='o', label='Enhanced Schnorr')
plt.plot(lambdas, avg_alg, marker='o', label='Algebraic Signature')
plt.title('Average tag generation time vs Security parameter')
plt.xlabel('Security parameter λ')
plt.ylabel('Average time per block (ms)')
plt.grid(True)
plt.legend()
plt.show()

# === Plot CSP proof generation time ===
plt.figure(figsize=(8,5))
plt.plot(lambdas, csp_basic, marker='o', label='Basic Schnorr')
plt.plot(lambdas, csp_enh, marker='o', label='Enhanced Schnorr')
plt.plot(lambdas, csp_alg, marker='o', label='Algebraic Signature')
plt.title('Average CSP proof generation time vs Security parameter')
plt.xlabel('Security parameter λ')
plt.ylabel('Average CSP proof gen time (ms)')
plt.grid(True)
plt.legend()
plt.show()

# === Plot TPA verification time ===
plt.figure(figsize=(8,5))
plt.plot(lambdas, tpa_basic, marker='o', label='Basic Schnorr')
plt.plot(lambdas, tpa_enh, marker='o', label='Enhanced Schnorr')
plt.plot(lambdas, tpa_alg, marker='o', label='Algebraic Signature')
plt.title('Average TPA verification time vs Security parameter')
plt.xlabel('Security parameter λ')
plt.ylabel('Average verification time (ms)')
plt.grid(True)
plt.legend()
plt.show()
