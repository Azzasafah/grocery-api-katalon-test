import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';
 
// Custom metric untuk error rate
const errorRate = new Rate('errors');
 
export const options = {
  // Staged load - mulai pelan, naik bertahap, turun
  stages: [
    { duration: '30s', target: 100 },  // Ramp up ke 100 VU dalam 30 detik
    { duration: '1m',  target: 500 },  // Naik ke 500 VU dalam 1 menit
    { duration: '2m',  target: 1000 }, // Naik ke 1000 VU dalam 2 menit
    { duration: '1m',  target: 1000 }, // Tahan di 1000 VU selama 1 menit
    { duration: '30s', target: 0 },    // Ramp down
  ],
 
  // Threshold = kondisi PASS/FAIL otomatis
  thresholds: {
    // 95% request harus selesai dalam 2 detik
    http_req_duration: ['p(95)<2000'],
    // Error rate harus di bawah 1%
    'errors': ['rate<0.01'],
    // Request failure rate
    http_req_failed: ['rate<0.01'],
  },
};
 
export default function () {
  // Request ke endpoint products dengan limit 20
  const res = http.get(
    'https://simple-grocery-store-api.click/products?results=20',
    {
      tags: { name: 'GetProducts' },  // Tag untuk grouping metrics
      timeout: '10s',
    }
  );
 
  // Assertions
  const checkResult = check(res, {
    'Status 200': (r) => r.status === 200,
    'Response tidak kosong': (r) => r.body.length > 0,
    'Response time < 2s': (r) => r.timings.duration < 2000,
    'Content-Type JSON': (r) => r.headers['Content-Type'].includes('application/json'),
  });
 
  // Catat error jika check gagal
  errorRate.add(!checkResult);
 
  // Sleep 1 detik antar iterasi (simulasi user nyata)
  sleep(1);
}
