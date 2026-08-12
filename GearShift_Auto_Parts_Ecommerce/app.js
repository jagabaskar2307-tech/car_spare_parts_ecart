const $ = s => document.querySelector(s);
const $$ = s => [...document.querySelectorAll(s)];
let state = {
  category: "All",
  search: "",
  cart: JSON.parse(localStorage.getItem("gearshift_cart") || "[]"),
  user: JSON.parse(localStorage.getItem("gearshift_user") || "null"),
  authMode: "login"
};

const money = n => new Intl.NumberFormat("en-IN",{style:"currency",currency:"INR",maximumFractionDigits:0}).format(n);

function saveCart(){localStorage.setItem("gearshift_cart",JSON.stringify(state.cart))}
function stars(r){return "★".repeat(Math.floor(r)) + (r%1>=.5?"½":"")}

function renderProducts(){
  const q = state.search.toLowerCase().trim();
  const filtered = products.filter(p =>
    (state.category==="All" || p.category===state.category) &&
    (!q || `${p.title} ${p.oem} ${p.category}`.toLowerCase().includes(q))
  );
  $("#resultCount").textContent = `${filtered.length} part${filtered.length===1?"":"s"}`;
  $("#emptyState").classList.toggle("hidden", filtered.length !== 0);
  $("#productGrid").innerHTML = filtered.map(p => `
    <article class="product-card">
      <div class="product-img">
        <img src="${p.image}" alt="${p.title}" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1486262715619-67b85e0b08d3?auto=format&fit=crop&w=900&q=80'">
        <span class="stock">${p.stock} in stock</span>
      </div>
      <div class="product-info">
        <div class="product-category">${p.category}</div>
        <h3>${p.title}</h3>
        <div class="oem">OEM: ${p.oem}</div>
        <div class="rating">${stars(p.rating)} <span>${p.rating}</span></div>
        <div class="product-bottom">
          <div class="price">${money(p.price)}</div>
          <button class="add-btn" data-add="${p.id}">Add to Cart</button>
        </div>
      </div>
    </article>`).join("");
  $$("[data-add]").forEach(b => b.addEventListener("click",()=>addToCart(+b.dataset.add)));
}

function addToCart(id){
  const existing = state.cart.find(i=>i.id===id);
  if(existing) existing.qty++;
  else state.cart.push({id,qty:1});
  saveCart(); renderCart(); openCart();
}

function changeQty(id,delta){
  const item=state.cart.find(i=>i.id===id);
  if(!item)return;
  item.qty += delta;
  if(item.qty<=0) state.cart=state.cart.filter(i=>i.id!==id);
  saveCart(); renderCart();
}

function removeItem(id){state.cart=state.cart.filter(i=>i.id!==id);saveCart();renderCart()}

function renderCart(){
  const count=state.cart.reduce((s,i)=>s+i.qty,0);
  const total=state.cart.reduce((s,i)=>{const p=products.find(x=>x.id===i.id);return s+(p?p.price*i.qty:0)},0);
  $("#cartCount").textContent=count;
  $("#cartTotal").textContent=money(total);
  $("#checkoutTotal").textContent=money(total);
  $("#cartItems").innerHTML = state.cart.length ? state.cart.map(i=>{
    const p=products.find(x=>x.id===i.id);
    return `<div class="cart-row">
      <img src="${p.image}" alt="">
      <div><h4>${p.title}</h4><small>${money(p.price)} each</small>
        <div class="qty"><button data-minus="${p.id}">−</button><b>${i.qty}</b><button data-plus="${p.id}">+</button>
        <button class="remove" data-remove="${p.id}">Remove</button></div>
      </div>
      <strong>${money(p.price*i.qty)}</strong>
    </div>`;
  }).join("") : `<div class="empty-state">Your cart is empty.<br>Add a part to get started.</div>`;
  $$("[data-minus]").forEach(b=>b.onclick=()=>changeQty(+b.dataset.minus,-1));
  $$("[data-plus]").forEach(b=>b.onclick=()=>changeQty(+b.dataset.plus,1));
  $$("[data-remove]").forEach(b=>b.onclick=()=>removeItem(+b.dataset.remove));
}

function openCart(){ $("#cartDrawer").classList.add("open"); $("#cartDrawer").setAttribute("aria-hidden","false"); $("#overlay").classList.add("show") }
function closeCart(){ $("#cartDrawer").classList.remove("open"); $("#cartDrawer").setAttribute("aria-hidden","true"); $("#overlay").classList.remove("show") }
function openModal(id){$(id).classList.remove("hidden")}
function closeModal(id){$(id).classList.add("hidden")}

function renderAuth(){
  if(state.user){
    $("#userGreeting").textContent=`Hi, ${state.user.name || state.user.email.split("@")[0]}`;
    $("#userGreeting").classList.remove("hidden");
    $("#authBtn").textContent="Logout";
  }else{
    $("#userGreeting").classList.add("hidden");
    $("#authBtn").textContent="Login";
  }
}

function setAuthMode(mode){
  state.authMode=mode;
  const register=mode==="register";
  $("#authEyebrow").textContent=register?"JOIN GEARSHIFT":"WELCOME BACK";
  $("#authTitle").textContent=register?"Create your account":"Sign in to GearShift";
  $("#authName").classList.toggle("hidden",!register);
  $("#authName").required=register;
  $("#authSubmit").textContent=register?"Create Account":"Login";
  $("#switchAuth").textContent=register?"Already have an account? Login":"New here? Create an account";
  $("#authError").textContent="";
}

$("#authBtn").onclick=()=>{
  if(state.user){
    state.user=null; localStorage.removeItem("gearshift_user"); renderAuth();
  }else{setAuthMode("login");openModal("#authModal")}
};
$("#heroLogin").onclick=()=>{setAuthMode("register");openModal("#authModal")};
$("#switchAuth").onclick=()=>setAuthMode(state.authMode==="login"?"register":"login");

$("#authForm").onsubmit=e=>{
  e.preventDefault();
  const email=$("#authEmail").value.trim(), password=$("#authPassword").value;
  if(password.length<6){$("#authError").textContent="Password must be at least 6 characters.";return}
  const name=$("#authName").value.trim() || email.split("@")[0];
  state.user={name,email};
  localStorage.setItem("gearshift_user",JSON.stringify(state.user));
  renderAuth(); closeModal("#authModal"); e.target.reset();
};

$("#searchInput").oninput=e=>{state.search=e.target.value;renderProducts()};
$("#categoryFilters").onclick=e=>{
  const b=e.target.closest(".filter"); if(!b)return;
  $$(".filter").forEach(x=>x.classList.remove("active")); b.classList.add("active");
  state.category=b.dataset.category;renderProducts();
};
$("#cartBtn").onclick=openCart; $("#closeCart").onclick=closeCart; $("#overlay").onclick=closeCart;

$("#checkoutBtn").onclick=()=>{
  if(!state.cart.length){alert("Your cart is empty.");return}
  if(!state.user){closeCart();setAuthMode("login");openModal("#authModal");$("#authError").textContent="Please login before checkout.";return}
  closeCart();
  $("#shipName").value=state.user.name||"";
  openModal("#checkoutModal");
};

$("#checkoutForm").onsubmit=e=>{
  e.preventDefault();
  const order="GS-"+Date.now().toString().slice(-8);
  $("#orderNumber").textContent=order;
  state.cart=[];saveCart();renderCart();closeModal("#checkoutModal");openModal("#successModal");e.target.reset();
};

$$("[data-close]").forEach(b=>b.onclick=()=>closeModal("#"+b.dataset.close));
$$(".modal-backdrop").forEach(m=>m.addEventListener("click",e=>{if(e.target===m)closeModal("#"+m.id)}));

$("#menuToggle").onclick=()=>$("#mainNav").classList.toggle("open");
$$("nav a").forEach(a=>a.onclick=()=>$("#mainNav").classList.remove("open"));

renderProducts();renderCart();renderAuth();
