import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';
 
const BASE_URL = 'https://simple-grocery-store-api.click';
 
// Custom metrics
const cartCreationErrors = new Rate('cart_creation_errors');
const addItemErrors      = new Rate('add_item_errors');
const cartFlowDuration   = new Trend('cart_flow_duration');
 
export const options = {
  stages: [
    { duration: '30s', target: 50 },   // Warmup
    { duration: '1m',  target: 200 },  // Naik ke 200 VU
    { duration: '2m',  target: 500 },  // Peak load
    { duration: '30s', target: 0 },    // Cooldown
  ],
  thresholds: {
    // Keseluruhan request harus sukses
    http_req_failed:        ['rate<0.05'],  // Toleransi 5% untuk flow test
    http_req_duration:      ['p(95)<3000'], // Flow boleh sedikit lebih lama
    // Custom metric cart flow
    'cart_flow_duration':   ['p(95)<5000'], // Seluruh flow < 5 detik
    'cart_creation_errors': ['rate<0.05'],
    'add_item_errors':      ['rate<0.05'],
  },
};
 
export default function () {
  const startTime = new Date();
 
  // === STEP 1: CREATE CART ===
  const cartRes = http.post(
    `${BASE_URL}/carts`,
    null,  // Tidak ada body
    { tags: { step: 'create_cart' }, timeout: '10s' }
  );
 
  const cartOk = check(cartRes, {
    '[Create Cart] Status 201': (r) => r.status === 201,
    '[Create Cart] Ada cartId': (r) => {
      try { return !!JSON.parse(r.body).cartId; }
      catch(e) { return false; }
    },
  });
 
  cartCreationErrors.add(!cartOk);
 
  // Jika create cart gagal, hentikan flow ini
  if (!cartOk || cartRes.status !== 201) {
    sleep(1);
    return;
  }
 
  // Ambil cartId dari response
  const cartId = JSON.parse(cartRes.body).cartId;
 
  sleep(0.5); // Jeda kecil antar step
 
  // === STEP 2: ADD ITEM TO CART ===
  const addRes = http.post(
    `${BASE_URL}/carts/${cartId}/items`,
    JSON.stringify({ productId: 4646 }),
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { step: 'add_item' },
      timeout: '10s',
    }
  );
 
  const addOk = check(addRes, {
    '[Add Item] Status 201': (r) => r.status === 201,
    '[Add Item] Item berhasil ditambahkan': (r) => {
      try { return JSON.parse(r.body).created === true; }
      catch(e) { return false; }
    },
  });
 
  addItemErrors.add(!addOk);
 
  // Catat total durasi flow
  const flowDuration = new Date() - startTime;
  cartFlowDuration.add(flowDuration);
 
  sleep(1);
}