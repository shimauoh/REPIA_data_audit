# REPIA_Data_Audit
Public Integrity Audit for Data in the Cloud Storage 

This project proposes a lightweight public data integrity auditing scheme based on an enhanced Schnorr signature. The scheme is designed for verifying the correctness of outsourced data stored in an untrusted public cloud environment using a Third-Party Auditor (TPA), with strong guarantees on data privacy, integrity, and performance.
## Key features:
- Enhanced Schnorr-based tag generation
- Lightweight CSP proof generation
- Efficient TPA verification
- Support for varying security parameters (λ = 128, 192, 256)
- Performance comparison against Algebraic Signature
  
## Repository Structure: 

📂 data-integrity-auditing/

├── key_generation/ # Java code for key generation

├── tag_generation/ # Tag generation scripts

├── proof_verification/ # TPA verification and CSP response code

├── results/ # CSV or JSON files with timing data

├── analysis/ #Python scripts for plotting and metrics

└── README.md # This file
## 📊 Experimental Results
The performance evaluation compares:
- Tag generation time
- CSP proof generation time
- TPA verification time

Across multiple block sizes: `256 B`, `512 B`, `1 KB`, `2 KB`, `4 KB`, `5 KB`  
And security parameters: `λ = 128, 192, 256 bits`
## 🧪 Requirements
JDK 8 or higher
Python 
## 📄 Citation
@article{shamiel2025Auditing,
  title={Robust and Efficient Public Data Integrity Auditing Scheme for Data in Public Cloud Storage},
  author={Sh. Ibrahim},
  journal={PLOS ONE},
  year={2025}
}

