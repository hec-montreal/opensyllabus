////////////////////////////////////////////////////////////////////////////////////////
// Big Integer Library v. 3.04
// (c)2000, 2001, 2002, 2003 Leemon Baird
// www.leemon.com
//
// I retain the copyright to this code, but you may redistribute
// or use it for any purpose.  If you use this code, please leave
// an acknowledgement and pointer to my home page in the comments.
// I make no claims about whether it works correctly, so use it
// at your own risk.
//
// This code defines a bigInt library for arbitrary-precision integers.
// A bigInt is an array of integers storing the value in bpe-bit chunks,
// little endian (buff[0] is the least significant word).
// Negative bigInts are stored two's complement.
// Some functions assume their parameters have at least one leading zero element.
// The results of all these functions are undefined in case of overflow,
// so the caller must make sure overflow won't happen.
// For each function where the first parameter x is modified, that same
// variable must not be used as another argument too.
// So, you cannot square x by doing multMod(x,x,n).
// You must use squareMod(x,n) instead, or use y=dup(x); multMod(x,y,n).
//
// These functions are designed to avoid frequent dynamic memory allocation in the inner loop.
// For most functions, if it needs a BigInt as a local variable it will actually use
// a global, and will only allocation to it when it's not the right size.  This ensures
// that when a function is called repeatedly with same-sized parameters, it only allocates
// memory on the first call.  The only exceptions to this rule are:
//     int2BigInt(), str2BigInt(), dup(), trim(), findPrimes()
//
// Note that for cryptographic purposes, the calls to Math.random() must
// be replaced with calls to a better pseudorandom number generator.
//
// In the following, "bigInt" means a bigInt with at least one leading zero element,
// and "integer" means a nonnegative integer less than radix.  In some cases, integer
// can be negative.
//
// function addInt(x,n)            //do x=x+n where x is a bigInt and n is an integer
// function add(x,y)               //do x=x+y for bigInts x and y
// function addShift(x,y,ys)       //do x=x+(y<<(ys*bpe))
// function copy(x,y)              //do x=y on bigInts x and y
// function copyInt(x,n)           //do x=y on bigInt x and integer n
// function bigInt2str(x,base)     //convert a bigInt into a string in a given base, from base 2 up to base 95
// function bitSize(x)             //returns how many bits long the bigInt x is, not counting leading zeros
// function carry(x)               //do carries and borrows so each element of the bigInt x fits in bpe bits.
// function divide(x,y,q,r)        //divide x by y giving quotient q and remainder r
// function divInt(x,n)            //do x=floor(x/n) for bigInt x and integer n, and return the remainder
// function dup(x)                 //returns a duplicate of bigInt x
// function eGCD(x,y,d,a,b)        //sets a,b,d to positive integers such that d = GCD(x,y) = a*x-b*y
// function equals(x,y)            //is the bigInt x equal to the bigint y?
// function equalsInt(x,y)         //is bigint x equal to integer y?
// function findPrimes(n)          //return array of all primes less than integer n
// function GCD(x,y)               //set x to the greatest common divisor of bigInts x and y, (y is destroyed).
// function greater(x,y)           //is x>y?  (x and y are nonnegative bigInts)
// function greaterShift(x,y,shift)//is (x <<(shift*bpe)) > y?
// function halve(x)               //do x=floor(|x|/2)*sgn(x) for bigInt x in 2's complement
// function int2bigInt(t,n,m)      //convert integer t to a bigInt with at least n bits and m array elements
// function inverseMod(x,n)        //do x=x**(-1) mod n, for bigInts x and n. Returns 1 (0) if inverse does (doesn't) exist
// function inverseModInt(x,n)     //return x**(-1) mod n, for integers x and n.  Return 0 if there is no inverse
// function isZero(x)              //is the bigInt x equal to zero?
// function leftShift(x,n)         //left shift bigInt x by n bits.  n<bpe.
// function linComb(x,y,a,b)       //do x=a*x+b*y for bigInts x and y and integers a and b
// function linCombShift(x,y,b,ys) //do x=x+b*(y<<(ys*bpe)) for bigInts x and y, and integers b and ys
// function millerRabin(x,b)       //does one round of Miller-Rabin base integer b say that bigInt x is possibly prime?
// function mod(x,n)               //do x=x mod n for bigInts x and n.
// function modInt(x,n)            //return x mod n for bigInt x and integer n.
// function mont(x,y,n,np)         //Montgomery multiplication (see comments where the function is defined)
// function mult(x,y)              //do x=x*y for bigInts x and y.
// function multInt(x,n)           //do x=x*n where x is a bigInt and n is an integer.
// function multMod(x,y,n)         //do x=x*y  mod n for bigInts x,y,n.
// function negative(x)            //is bigInt x negative?
// function powMod(x,y,n)          //do x=x**y mod n, where x,y,n are bigInts (n is odd) and ** is exponentiation.  0**0=1.
// function randBigInt(b,n,s)      //Generate an n-bit random BigInt. if s=1, then nth bit (most significant bit) is set to 1
// function randTruePrime(ans,k)   //ans = a random k-bit true random prime (not just probable prime) with 1 in the msb.
// function rightShift(x,n)        //right shift bigInt x by n bits.  n<bpe.
// function squareMod(x,n)         //do x=x*x  mod n for bigInts x,n
// function str2bigInt(s,b,n,m)    //convert string s in base b to a bigInt with at least n bits and m array elements
// function sub(x,y)               //do x=x-y for bigInts x and y
// function subShift(x,y,ys)       //do x=x-(y<<(ys*bpe))
// function trim(x,k)              //return x with exactly k leading zeros
//
// The following functions are based on algorithms from the _Handbook of Applied Cryptography_
//    powMod()          = algorithm 14.94, Montgomery exponentiation
//    eGCD,inverseMod() = algorithm 14.61, Binary extended GCD
//    GCD()             = algorothm 14.57, Lehmer's algorithm
//    mont()            = algorithm 14.36, Montgomery multiplication
//    divide()          = algorithm 14.20  Multiple-precision division
//    squareMod()       = algorithm 14.16  Multiple-precision squaring
//    randTruePrime()   = algorithm  4.62, Maurer's algorithm
//    millerRabin()     = algorithm  4.24, Miller-Rabin algorithm
//
// Profiling shows:
//     randTruePrime() spends:
//         10% of its time in calls to powMod()
//         85% of its time in calls to millerRabin()
//     millerRabin() spends:
//         99% of its time in calls to powMod()   (always with a base of 2)
//     powMod() spends:
//         94% of its time in calls to mont()  (almost always with x==y)
//
// This suggests there are several ways to speed up this library slightly:
//     - convert powMod to use a Montgomery form of k-ary window (or maybe a Montgomery form of sliding window)
//         -- this should especially focus on being fast when raising 2 to a power mod n
//     - convert randTruePrime() to use a minimum r of 1/3 instead of 1/2 with the appropriate change to the test
//     - tune the parameters in randTruePrime(), including c, m, and recLimit
//     - speed up the single loop in mont() that takes 95% of the runtime, perhaps by reducing checking
//       within the loop when all the parameters are the same length.
//
// There are several ideas that look like they wouldn't help much at all:
//     - replacing trial division in randTruePrime() with a sieve (that speeds up something taking almost no time anyway)
//     - increase bpe from 15 to 30 (that would help if we had a 32*32->64 multiplier, but not with JavaScript's 32*32->32)
//     - speeding up mont(x,y,n,np) when x==y by doing a non-modular, non-Montgomery square
//       followed by a Montgomery reduction.  The intermediate answer will be twice as long as x, so that
//       method would be slower.  This is unfortunate because the code currently spends almost all of its time
//       doing mont(x,x,...), both for randTruePrime() and powMod().  A faster method for Montgomery squaring
//       would have a large impact on the speed of randTruePrime() and powMod().  HAC has a couple of poorly-worded
//       sentences that seem to imply it's faster to do a non-modular square followed by a single
//       Montgomery reduction, but that's obviously wrong.
////////////////////////////////////////////////////////////////////////////////////////

//globals
bpe=0;         //bits stored per array element
mask=0;        //AND this with an array element to chop it down to bpe bits
radix=mask+1;  //equals 2^bpe.  A single 1 bit to the left of the last bit of mask.

//the digits for converting to different bases
digitsStr='0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_=!@#$%^&*()[]{}|;:,.<>/?`~ \\\'\"+-';

//initialize the global variables
for (bpe=0; (1<<(bpe+1)) > (1<<bpe); bpe++);  //bpe=number of bits in the mantissa on this platform
bpe>>=1;                   //bpe=number of bits in one element of the array representing the bigInt
mask=(1<<bpe)-1;           //AND the mask with an integer to get its bpe least-significant bits
radix=mask+1;              //2^bpe.  a single 1 bit to the left of the first bit of mask
one=int2bigInt(1,1,1);     //constant used in powMod()

//the following global variables are scratchpad memory to
//reduce dynamic memory allocation in the inner loop
t=new Array(0);
ss=t;       //used in mult()
s0=t;       //used in multMod(), squareMod()
s1=t;       //used in powMod(), multMod(), squareMod()
s2=t;       //used in powMod(), multMod()
s3=t;       //used in powMod()
s4=t; s5=t; //used in mod()
s6=t;       //used in bigInt2str()
s7=t;       //used in powMod()
T=t;        //used in GCD()
sa=t;       //used in mont()
mr_x1=t; mr_r=t; mr_a=t;                                      //used in millerRabin()
eg_v=t; eg_u=t; eg_A=t; eg_B=t; eg_C=t; eg_D=t;               //used in eGCD(), inverseMod()
md_q1=t; md_q2=t; md_q3=t; md_r=t; md_r1=t; md_r2=t; md_tt=t; //used in mod()

primes=t; pows=t; s_i=t; s_i2=t; s_R=t; s_rm=t; s_q=t; s_n1=t;
  s_a=t; s_r2=t; s_n=t; s_b=t; s_d=t; s_x1=t; s_x2=t, s_aa=t; //used in randTruePrime()

////////////////////////////////////////////////////////////////////////////////////////

//return array of all primes less than integer n
function findPrimes(n) {
  var i,s,p,ans;
  s=new Array(n);
  for (i=0;i<n;i++)
    s[i]=0;
  s[0]=2;
  p=0;    //first p elements of s are primes, the rest are a sieve
  for(;s[p]<n;) {                  //s[p] is the pth prime
    for(i=s[p]*s[p]; i<n; i+=s[p]) //mark multiples of s[p]
      s[i]=1;
    p++;
    s[p]=s[p-1]+1;
    for(; s[p]<n && s[s[p]]; s[p]++); //find next prime (where s[p]==0)
  }
  ans=new Array(p);
  for(i=0;i<p;i++)
    ans[i]=s[i];
  return ans;
}

//does a single round of Miller-Rabin base b consider x to be a possible prime?
//x is a bigInt, and b is an integer
function millerRabin(x,b) {
  var i,j,k,s;

  if (mr_x1.length!=x.length) {
    mr_x1=dup(x);
    mr_r=dup(x);
    mr_a=dup(x);
  }

  copyInt(mr_a,b);
  copy(mr_r,x);
  copy(mr_x1,x);

  addInt(mr_r,-1);
  addInt(mr_x1,-1);

  //s=the highest power of two that divides mr_r
  k=0;
  for (i=0;i<mr_r.length;i++)
    for (j=1;j<mask;j<<=1)
      if (x[i] & j) {
        s=(k<mr_r.length+bpe ? k : 0);
         i=mr_r.length;
         j=mask;
      } else
        k++;

  if (s)
    rightShift(mr_r,s);

  powMod(mr_a,mr_r,x);

  if (!equalsInt(mr_a,1) && !equals(mr_a,mr_x1)) {
    j=1;
    while (j<=s-1 && !equals(mr_a,mr_x1)) {
      squareMod(mr_a,x);
      if (equalsInt(mr_a,1)) {
        return 0;
      }
      j++;
    }
    if (!equals(mr_a,mr_x1)) {
      return 0;
    }
  }
  return 1;
}

//returns how many bits long the bigInt is, not counting leading zeros.
function bitSize(x) {
  var j,z,w;
  for (j=x.length-1; (x[j]==0) && (j>0); j--);
  for (z=0,w=x[j]; w; (w>>=1),z++);
  z+=bpe*j;
  return z;
}

//generate a k-bit true random prime using Maurer's algorithm,
//and put it into ans.  The bigInt ans must be large enough to hold it.
function randTruePrime(ans,k) {
  var c,m,pm,dd,j,r,B,divisible,z,zz,recSize;

  if (primes.length==0)
    primes=findPrimes(30000);  //check for divisibility by primes <=30000

  if (pows.length==0) {
    pows=new Array(512);
    for (j=0;j<512;j++) {
      pows[j]=Math.pow(2,j/511.-1.);
    }
  }

  //c and m should be tuned for a particular machine and value of k, to maximize speed
  //this was:   c=primes[primes.length-1]/k/k;  //check using all the small primes.  (c=0.1 in HAC)
  c=0.1;
  m=20;   //generate this k-bit number by first recursively generating a number that has between k/2 and k-m bits
  recLimit=20; /*must be at least 2 (was 29)*/   //stop recursion when k <=recLimit

  if (s_i2.length!=ans.length) {
    s_i2=dup(ans);
    s_R =dup(ans);
    s_n1=dup(ans);
    s_r2=dup(ans);
    s_d =dup(ans);
    s_x1=dup(ans);
    s_x2=dup(ans);
    s_b =dup(ans);
    s_n =dup(ans);
    s_i =dup(ans);
    s_rm=dup(ans);
    s_q =dup(ans);
    s_a =dup(ans);
    s_aa=dup(ans);
  }

  if (k <= recLimit) {  //generate small random primes by trial division up to its square root
    pm=(1<<((k+2)>>1))-1; //pm is binary number with all ones, just over sqrt(2^k)
    copyInt(ans,0);
    for (dd=1;dd;) {
      dd=0;
      ans[0]= 1 | (1<<(k-1)) | Math.floor(Math.random()*(1<<k));  //random, k-bit, odd integer, with msb 1
      for (j=1;(j<primes.length) && ((primes[j]&pm)==primes[j]);j++) { //trial division by all primes 3...sqrt(2^k)
        if (0==(ans[0]%primes[j])) {
          dd=1;
          break;
        }
      }
    }
    carry(ans);
    return;
  }

  B=c*k*k;    //try small primes up to B (or all the primes[] array if the largest is less than B).
  if (k>2*m)  //generate this k-bit number by first recursively generating a number that has between k/2 and k-m bits
    for (r=1; k-k*r<=m; )
      r=pows[Math.floor(Math.random()*512)];   //r=Math.pow(2,Math.random()-1);
  else
    r=.5;

  //simulation suggests the more complex algorithm using r=.333 is only slightly faster.

  recSize=Math.floor(r*k)+1;

  randTruePrime(s_q,recSize);
  copyInt(s_i2,0);
  s_i2[Math.floor((k-2)/bpe)] |= (1<<((k-2)%bpe));   //s_i2=2^(k-2)
  divide(s_i2,s_q,s_i,s_rm);                         //s_i=floor((2^(k-1))/(2q))

  z=bitSize(s_i);

  for (;;) {
    for (;;) {  //generate z-bit numbers until one falls in the range [0,s_i-1]
      randBigInt(s_R,z,0);
      if (greater(s_i,s_R))
        break;
    }               //now s_R is in the range [0,s_i-1]
    addInt(s_R,1);  //now s_R is in the range [1,s_i]
    add(s_R,s_i);   //now s_R is in the range [s_i+1,2*s_i]

    copy(s_n,s_q);
    mult(s_n,s_R);
    multInt(s_n,2);
    addInt(s_n,1);    //s_n=2*s_R*s_q+1

    copy(s_r2,s_R);
    multInt(s_r2,2);  //s_r2=2*s_R

    //check s_n for divisibility by small primes up to B
    for (divisible=0,j=0; (j<primes.length) && (primes[j]<B); j++)
      if (modInt(s_n,primes[j])==0) {
        divisible=1;
        break;
      }

    if (!divisible)    //if it passes small primes check, then try a single Miller-Rabin base 2
      if (!millerRabin(s_n,2)) //this line represents 75% of the total runtime for randTruePrime
        divisible=1;

    if (!divisible) {  //if it passes that test, continue checking s_n
      addInt(s_n,-3);
      for (j=s_n.length-1;(s_n[j]==0) && (j>0); j--);  //strip leading zeros
      for (zz=0,w=s_n[j]; w; (w>>=1),zz++);
      zz+=bpe*j;                             //zz=number of bits in s_n, ignoring leading zeros
      for (;;) {  //generate z-bit numbers until one falls in the range [0,s_n-1]
        randBigInt(s_a,zz,0);
        if (greater(s_n,s_a))
          break;
      }               //now s_a is in the range [0,s_n-1]
      addInt(s_n,3);  //now s_a is in the range [0,s_n-4]
      addInt(s_a,2);  //now s_a is in the range [2,s_n-2]
      copy(s_b,s_a);
      copy(s_n1,s_n);
      addInt(s_n1,-1);
      powMod(s_b,s_n1,s_n);   //s_b=s_a^(s_n-1) modulo s_n
      addInt(s_b,-1);
      if (isZero(s_b)) {
        copy(s_b,s_a);
        powMod(s_b,s_r2,s_n);
        addInt(s_b,-1);
        copy(s_aa,s_n);
        copy(s_d,s_b);
        GCD(s_d,s_n);  //if s_b and s_n are relatively prime, then s_n is a prime
        if (equalsInt(s_d,1)) {
          copy(ans,s_aa);
          return;     //if we've made it this far, then s_n is absolutely guaranteed to be prime
        }
      }
    }
  }
}

//Generate an n-bit random BigInt.  If s=1, then nth bit (most significant bit) is set to 1
//array b must be big enough to hold the result. Must have n>=1
function randBigInt(b,n,s) {
  var i,a;
  for (i=0;i<b.length;i++)
    b[i]=0;
  a=Math.floor((n-1)/bpe)+1; //# array elements to hold the BigInt
  for (i=0;i<a;i++) {
    b[i]=Math.floor(Math.random()*(1<<(bpe-1)));
  }
  b[a-1] &= (2<<((n-1)%bpe))-1;
  if (s)
    b[a-1] |= (1<<((n-1)%bpe));
}

//set x to the greatest common divisor of x and y.
//x,y are bigInts with the same number of elements.  y is destroyed.
function GCD(x,y) {
  var i,xp,yp,A,B,C,D,q,sing;
  if (T.length!=x.length)
    T=dup(x);

  sing=1;
  while (sing) { //while y has nonzero elements other than y[0]
    sing=0;
    for (i=1;i<y.length;i++) //check if y has nonzero elements other than 0
      if (y[i]) {
        sing=1;
        break;
      }
    if (!sing) break; //quit when y all zero elements except possibly y[0]

    for (i=x.length;!x[i] && i>=0;i--);  //find most significant element of x
    xp=x[i];
    yp=y[i];
    A=1; B=0; C=0; D=1;
    while ((yp+C) && (yp+D)) {
      q =Math.floor((xp+A)/(yp+C));
      qp=Math.floor((xp+B)/(yp+D));
      if (q!=qp)
        break;
      t= A-q*C;   A=C;   C=t;    //  do (A,B,xp, C,D,yp) = (C,D,yp, A,B,xp) - q*(0,0,0, C,D,yp)
      t= B-q*D;   B=D;   D=t;
      t=xp-q*yp; xp=yp; yp=t;
    }
    if (B) {
      copy(T,x);
      linComb(x,y,A,B); //x=A*x+B*y
      linComb(y,T,D,C); //y=D*y+C*T
    } else {
      mod(x,y);
      copy(T,x);
      copy(x,y);
      copy(y,T);
    }
  }
  if (y[0]==0)
    return;
  t=modInt(x,y[0]);
  copyInt(x,y[0]);
  y[0]=t;
  while (y[0]) {
    x[0]%=y[0];
    t=x[0]; x[0]=y[0]; y[0]=t;
  }
}

//do x=x**(-1) mod n, for bigInts x and n.
//If no inverse exists, it sets x to zero and returns 0, else it returns 1.
//The x array must be at least as large as the n array.
function inverseMod(x,n) {
  var k=1+2*Math.max(x.length,n.length);

  if(!(x[0]&1)  && !(n[0]&1)) {  //if both inputs are even, then inverse doesn't exist
    copyInt(x,0);
    return 0;
  }

  if (eg_u.length!=k) {
    eg_u=new Array(k);
    eg_v=new Array(k);
    eg_A=new Array(k);
    eg_B=new Array(k);
    eg_C=new Array(k);
    eg_D=new Array(k);
  }

  copy(eg_u,x);
  copy(eg_v,n);
  copyInt(eg_A,1);
  copyInt(eg_B,0);
  copyInt(eg_C,0);
  copyInt(eg_D,1);
  for (;;) {
    while(!(eg_u[0]&1)) {  //while eg_u is even
      halve(eg_u);
      if (!(eg_A[0]&1) && !(eg_B[0]&1)) { //if eg_A==eg_B==0 mod 2
        halve(eg_A);
        halve(eg_B);
      } else {
        add(eg_A,n);  halve(eg_A);
        sub(eg_B,x);  halve(eg_B);
      }
    }

    while (!(eg_v[0]&1)) {  //while eg_v is even
      halve(eg_v);
      if (!(eg_C[0]&1) && !(eg_D[0]&1)) { //if eg_C==eg_D==0 mod 2
        halve(eg_C);
        halve(eg_D);
      } else {
        add(eg_C,n);  halve(eg_C);
        sub(eg_D,x);  halve(eg_D);
      }
    }

    if (!greater(eg_v,eg_u)) { //eg_v <= eg_u
      sub(eg_u,eg_v);
      sub(eg_A,eg_C);
      sub(eg_B,eg_D);
    } else {                   //eg_v > eg_u
      sub(eg_v,eg_u);
      sub(eg_C,eg_A);
      sub(eg_D,eg_B);
    }

    if (equalsInt(eg_u,0)) {
      if (negative(eg_C)) //make sure answer is nonnegative
        add(eg_C,n);
      copy(x,eg_C);

      if (!equalsInt(eg_v,1)) { //if GCD(x,n)!=1, then there is no inverse
        copyInt(x,0);
        return 0;
      }
      return 1;
    }
  }
}

//return x**(-1) mod n, for integers x and n.  Return 0 if there is no inverse
function inverseModInt(x,n) {
  var a=1,b=0,t;
  for (;;) {
    if (x==1) return a;
    if (x==0) return 0;
    b-=a*Math.floor(n/x);
    n%=x;

    if (n==1) return b; //to avoid negatives, change this b to n-b, and each -= to +=
    if (n==0) return 0;
    a-=b*Math.floor(x/n);
    x%=n;
  }
}

//Given positive bigInts x and y, change the bigints v, a, and b to positive bigInts such that:
//     v = GCD(x,y) = a*x-b*y
//The bigInts v, a, b, must have exactly as many elements as the larger of x and y.
function eGCD(x,y,v,a,b) {
  var g=0;
  var k=Math.max(x.length,y.length);
  if (eg_u.length!=k) {
    eg_u=new Array(k);
    eg_A=new Array(k);
    eg_B=new Array(k);
    eg_C=new Array(k);
    eg_D=new Array(k);
  }
  while(!(x[0]&1)  && !(y[0]&1)) {  //while x and y both even
    halve(x);
    halve(y);
    g++;
  }
  copy(eg_u,x);
  copy(v,y);
  copyInt(eg_A,1);
  copyInt(eg_B,0);
  copyInt(eg_C,0);
  copyInt(eg_D,1);
  for (;;) {
    while(!(eg_u[0]&1)) {  //while u is even
      halve(eg_u);
      if (!(eg_A[0]&1) && !(eg_B[0]&1)) { //if A==B==0 mod 2
        halve(eg_A);
        halve(eg_B);
      } else {
        add(eg_A,y);  halve(eg_A);
        sub(eg_B,x);  halve(eg_B);
      }
    }

    while (!(v[0]&1)) {  //while v is even
      halve(v);
      if (!(eg_C[0]&1) && !(eg_D[0]&1)) { //if C==D==0 mod 2
        halve(eg_C);
        halve(eg_D);
      } else {
        add(eg_C,y);  halve(eg_C);
        sub(eg_D,x);  halve(eg_D);
      }
    }

    if (!greater(v,eg_u)) { //v<=u
      sub(eg_u,v);
      sub(eg_A,eg_C);
      sub(eg_B,eg_D);
    } else {                //v>u
      sub(v,eg_u);
      sub(eg_C,eg_A);
      sub(eg_D,eg_B);
    }
    if (equalsInt(eg_u,0)) {
      if (negative(eg_C)) {   //make sure a (C)is nonnegative
        add(eg_C,y);
        sub(eg_D,x);
      }
      multInt(eg_D,-1);  ///make sure b (D) is nonnegative
      copy(a,eg_C);
      copy(b,eg_D);
      leftShift(v,g);
      return;
    }
  }
}


//is bigInt x negative?
function negative(x) {
  return ((x[x.length-1]>>(bpe-1))&1);
}


//is (x << (shift*bpe)) > y?
//x and y are nonnegative bigInts
//shift is a nonnegative integer
function greaterShift(x,y,shift) {
  var kx=x.length, ky=y.length;
  k=((kx+shift)<ky) ? (kx+shift) : ky;
  for (i=ky-1-shift; i<kx && i>=0; i++)
    if (x[i]>0)
      return 1; //if there are nonzeros in x to the left of the first column of y, then x is bigger
  for (i=kx-1+shift; i<ky; i++)
    if (y[i]>0)
      return 0; //if there are nonzeros in y to the left of the first column of x, then x is not bigger
  for (i=k-1; i>=shift; i--)
    if      (x[i-shift]>y[i]) return 1;
    else if (x[i-shift]<y[i]) return 0;
  return 0;
}

//is x > y? (x and y both nonnegative)
function greater(x,y) {
  var i;
  var k=(x.length<y.length) ? x.length : y.length;

  for (i=x.length;i<y.length;i++)
    if (y[i])
      return 0;  //y has more digits

  for (i=y.length;i<x.length;i++)
    if (x[i])
      return 1;  //x has more digits

  for (i=k-1;i>=0;i--)
    if (x[i]>y[i])
      return 1;
    else if (x[i]<y[i])
      return 0;
  return 0;
}

//divide x by y giving quotient q and remainder r.  (q=floor(x/y),  r=x mod y).  All 4 are bigints.
//x must have at least one leading zero element.
//y must be nonzero.
//q and r must be arrays that are exactly the same length as x.
//the x array must have at least as many elements as y.
function divide(x,y,q,r) {
  var kx, ky;
  var i,j,y1,y2,c,a,b;
  copy(r,x);
  for (ky=y.length;y[ky-1]==0;ky--); //kx,ky is number of elements in x,y, not including leading zeros
  for (kx=r.length;r[kx-1]==0 && kx>ky;kx--);

  //normalize: ensure the most significant element of y has its highest bit set
  b=y[ky-1];
  for (a=0; b; a++)
    b>>=1;
  a=bpe-a;  //a is how many bits to shift so that the high order bit of y is leftmost in its array element
  leftShift(y,a);  //multiply both by 1<<a now, then divide both by that at the end
  leftShift(r,a);

  copyInt(q,0);                // q=0
  while (!greaterShift(y,r,kx-ky)) {  // while (leftShift(y,kx-ky) <= r) {
    subShift(r,y,kx-ky);      //   r=r-leftShift(y,kx-ky)
    q[kx-ky]++;                  //   q[kx-ky]++;
  }                              // }

  for (i=kx-1; i>=ky; i--) {
    if (r[i]==y[ky-1])
      q[i-ky]=mask;
    else
      q[i-ky]=Math.floor((r[i]*radix+r[i-1])/y[ky-1]);

    //The following for(;;) loop is equivalent to the commented while loop,
    //except that the uncommented version avoids overflow.
    //The commented loop comes from HAC, which assumes r[-1]==y[-1]==0
    //  while (q[i-ky]*(y[ky-1]*radix+y[ky-2]) > r[i]*radix*radix+r[i-1]*radix+r[i-2])
    //    q[i-ky]--;
    for (;;) {
      y2=(ky>1 ? y[ky-2] : 0)*q[i-ky];
      c=y2>>bpe;
      y2=y2 & mask;
      y1=c+q[i-ky]*y[ky-1];
      c=y1>>bpe;
      y1=y1 & mask;

      if (c==r[i] ? y1==r[i-1] ? y2>(i>1 ? r[i-2] : 0) : y1>r[i-1] : c>r[i])
        q[i-ky]--;
      else
        break;
    }

    linCombShift(r,y,-q[i-ky],i-ky);    //r=r-q[i-ky]*leftShift(y,i-ky)
    if (negative(r)) {
      addShift(r,y,i-ky);         //r=r+leftShift(y,i-ky)
      q[i-ky]--;
    }
  }

  rightShift(y,a);  //undo the normalization step
  rightShift(r,a);  //undo the normalization step
}

//do carries and borrows so each element of the bigInt x fits in bpe bits.
function carry(x) {
  var i,k,c,b;
  k=x.length;
  c=0;
  for (i=0;i<k;i++) {
    c+=x[i];
    b=0;
    if (c<0) {
      b=-(c>>bpe);
      c+=b*radix;
    }
    x[i]=c & mask;
    c=(c>>bpe)-b;
  }
}

//return x mod n for bigInt x and integer n.
function modInt(x,n) {
  var i,c=0;
  for (i=x.length-1; i>=0; i--)
    c=(c*radix+x[i])%n;
  return c;
}

//convert the integer t into a bigInt with at least the given number of bits.
//the returned array stores the bigInt in bpe-bit chunks, little endian (buff[0] is least significant word)
//Pad the array with leading zeros so that it has at least minSize elements.
//There will always be at least one leading 0 element.
function int2bigInt(t,bits,minSize) {
  var i,k;
  k=Math.ceil(bits/bpe)+1;
  k=minSize>k ? minSize : k;
  buff=new Array(k);
  copyInt(buff,t);
  return buff;
}



//return the bigInt given a string representation in a given base.
//Pad the array with leading zeros so that it has at least minSize elements.
//If base=-1, then it reads in a space-separated list of array elements in decimal.
//The array will always have at least one leading zero, unless base=-1.
function str2bigInt(s,base,minSize) {
  var d, i, j, x, y, kk;
  var k=s.length;
  if (base==-1) { //comma-separated list of array elements in decimal
    x=new Array(0);
    for (;;) {
      y=new Array(x.length+1);
      for (i=0;i<x.length;i++)
        y[i+1]=x[i];
      y[0]=parseInt(s,10);
      x=y;
      d=s.indexOf(',',0);
      if (d<1)
        break;
      s=s.substring(d+1);
      if (s.length==0)
        break;
    }
    if (x.length<minSize) {
      y=new Array(minSize);
      copy(y,x);
      return y;
    }
    return x;
  }

  x=int2bigInt(0,base*k,0);
  for (i=0;i<k;i++) {
    d=digitsStr.indexOf(s.substring(i,i+1),0);
    if (base<=36 && d>=36)  //convert lowercase to uppercase if base<=36
      d-=26;
    if (d<base && d>=0) {   //ignore illegal characters
      multInt(x,base);
      addInt(x,d);
    }
  }
  for (k=x.length;k>0 && !x[k-1];k--); //strip off leading zeros
  k=minSize>k+1 ? minSize : k+1;
  y=new Array(k);
  kk=k<x.length ? k : x.length;
  for (i=0;i<kk;i++)
    y[i]=x[i];
  for (;i<k;i++)
    y[i]=0;
  return y;
}

//is bigint x equal to integer y?
//y must have less than bpe bits
function equalsInt(x,y) {
  var i;
  if (x[0]!=y)
    return 0;
  for (i=1;i<x.length;i++)
    if (x[i])
      return 0;
  return 1;
}

//are bigints x and y equal?
//this works even if x and y are different lengths and have arbitrarily many leading zeros
function equals(x,y) {
  var i;
  var k=x.length<y.length ? x.length : y.length;
  for (i=0;i<k;i++)
    if (x[i]!=y[i])
      return 0;
  if (x.length>y.length) {
    for (;i<x.length;i++)
      if (x[i])
        return 0;
  } else {
    for (;i<y.length;i++)
      if (y[i])
        return 0;
  }
  return 1;
}

//is the bigInt x equal to zero?
function isZero(x) {
  var i;
  for (i=0;i<x.length;i++)
    if (x[i])
      return 0;
  return 1;
}

//convert a bigInt into a string in a given base, from base 2 up to base 95.
//Base -1 prints the contents of the array representing the number.
function bigInt2str(x,base) {
  var i,t,s="";

  if (s6.length!=x.length)
    s6=dup(x);
  else
    copy(s6,x);

  if (base==-1) { //return the list of array contents
    for (i=x.length-1;i>0;i--)
      s+=x[i]+',';
    s+=x[0];
  }
  else { //return it in the given base
    while (!isZero(s6)) {
      t=divInt(s6,base);  //t=s6 % base; s6=floor(s6/base);
      s=digitsStr.substring(t,t+1)+s;
    }
  }
  if (s.length==0)
    s="0";
  return s;
}

//returns a duplicate of bigInt x
function dup(x) {
  var i;
  buff=new Array(x.length);
  copy(buff,x);
  return buff;
}

//do x=y on bigInts x and y.  x must be an array at least as big as y (not counting the leading zeros in y).
function copy(x,y) {
  var i;
  var k=x.length<y.length ? x.length : y.length;
  for (i=0;i<k;i++)
    x[i]=y[i];
  for (i=k;i<x.length;i++)
    x[i]=0;
}

//do x=y on bigInt x and integer y.
function copyInt(x,n) {
  var i,c;
  for (c=n,i=0;i<x.length;i++) {
    x[i]=c & mask;
    c>>=bpe;
  }
}

//do x=x+n where x is a bigInt and n is an integer.
//x must be large enough to hold the result.
function addInt(x,n) {
  var i,k,c,b;
  x[0]+=n;
  k=x.length;
  c=0;
  for (i=0;i<k;i++) {
    c+=x[i];
    b=0;
    if (c<0) {
      b=-(c>>bpe);
      c+=b*radix;
    }
    x[i]=c & mask;
    c=(c>>bpe)-b;
    if (!c) return; //stop carrying as soon as the carry is zero
  }
}

//right shift bigInt x by n bits.  n<bpe.
function rightShift(x,n) {
  var i;
  var k=Math.floor(n/bpe);
  if (k) {
    for (i=0;i<x.length-k;i++) //right shift x by k elements
      x[i]=x[i+k];
    for (;i<x.length;i++)
      x[i]=0;
    n%=bpe;
  }
  for (i=0;i<x.length-1;i++) {
    x[i]=mask & ((x[i+1]<<(bpe-n)) | (x[i]>>n));
  }
  x[i]>>=n;
}

//do x=floor(|x|/2)*sgn(x) for bigInt x in 2's complement
function halve(x) {
  var i;
  for (i=0;i<x.length-1;i++) {
    x[i]=mask & ((x[i+1]<<(bpe-1)) | (x[i]>>1));
  }
  x[i]=(x[i]>>1) | (x[i] & (radix>>1));  //most significant bit stays the same
}

//left shift bigInt x by n bits.
function leftShift(x,n) {
  var i;
  var k=Math.floor(n/bpe);
  if (k) {
    for (i=x.length; i>=k; i--) //left shift x by k elements
      x[i]=x[i-k];
    for (;i>=0;i--)
      x[i]=0;
    n%=bpe;
  }
  if (!n)
    return;
  for (i=x.length-1;i>0;i--) {
    x[i]=mask & ((x[i]<<n) | (x[i-1]>>(bpe-n)));
  }
  x[i]=mask & (x[i]<<n);
}

//do x=x*n where x is a bigInt and n is an integer.
//x must be large enough to hold the result.
function multInt(x,n) {
  var i,k,c,b;
  if (!n)
    return;
  k=x.length;
  c=0;
  for (i=0;i<k;i++) {
    c+=x[i]*n;
    b=0;
    if (c<0) {
      b=-(c>>bpe);
      c+=b*radix;
    }
    x[i]=c & mask;
    c=(c>>bpe)-b;
  }
}

//do x=floor(x/n) for bigInt x and integer n, and return the remainder
function divInt(x,n) {
  var i,r=0,s;
  for (i=x.length-1;i>=0;i--) {
    s=r*radix+x[i];
    x[i]=Math.floor(s/n);
    r=s%n;
  }
  return r;
}

//do the linear combination x=a*x+b*y for bigInts x and y, and integers a and b.
//x must be large enough to hold the answer.
function linComb(x,y,a,b) {
  var i,c,k,kk;
  k=x.length<y.length ? x.length : y.length;
  kk=x.length;
  for (c=0,i=0;i<k;i++) {
    c+=a*x[i]+b*y[i];
    x[i]=c & mask;
    c>>=bpe;
  }
  for (i=k;i<kk;i++) {
    c+=a*x[i];
    x[i]=c & mask;
    c>>=bpe;
  }
}

//do the linear combination x=a*x+b*(y<<(ys*bpe)) for bigInts x and y, and integers a, b and ys.
//x must be large enough to hold the answer.
function linCombShift(x,y,b,ys) {
  var i,c,k,kk;
  k=x.length<ys+y.length ? x.length : ys+y.length;
  kk=x.length;
  for (c=0,i=ys;i<k;i++) {
    c+=x[i]+b*y[i-ys];
    x[i]=c & mask;
    c>>=bpe;
  }
  for (i=k;c && i<kk;i++) {
    c+=x[i];
    x[i]=c & mask;
    c>>=bpe;
  }
}

//do x=x+(y<<(ys*bpe)) for bigInts x and y, and integers a,b and ys.
//x must be large enough to hold the answer.
function addShift(x,y,ys) {
  var i,c,k,kk;
  k=x.length<ys+y.length ? x.length : ys+y.length;
  kk=x.length;
  for (c=0,i=ys;i<k;i++) {
    c+=x[i]+y[i-ys];
    x[i]=c & mask;
    c>>=bpe;
  }
  for (i=k;c && i<kk;i++) {
    c+=x[i];
    x[i]=c & mask;
    c>>=bpe;
  }
}

//do x=x-(y<<(ys*bpe)) for bigInts x and y, and integers a,b and ys.
//x must be large enough to hold the answer.
function subShift(x,y,ys) {
  var i,c,k,kk;
  k=x.length<ys+y.length ? x.length : ys+y.length;
  kk=x.length;
  for (c=0,i=ys;i<k;i++) {
    c+=x[i]-y[i-ys];
    x[i]=c & mask;
    c>>=bpe;
  }
  for (i=k;c && i<kk;i++) {
    c+=x[i];
    x[i]=c & mask;
    c>>=bpe;
  }
}

//do x=x-y for bigInts x and y.
//x must be large enough to hold the answer.
//negative answers will be 2s complement
function sub(x,y) {
  var i,c,k,kk;
  k=x.length<y.length ? x.length : y.length;
  for (c=0,i=0;i<k;i++) {
    c+=x[i]-y[i];
    x[i]=c & mask;
    c>>=bpe;
  }
  for (i=k;c && i<x.length;i++) {
    c+=x[i];
    x[i]=c & mask;
    c>>=bpe;
  }
}

//do x=x+y for bigInts x and y.
//x must be large enough to hold the answer.
function add(x,y) {
  var i,c,k,kk;
  k=x.length<y.length ? x.length : y.length;
  for (c=0,i=0;i<k;i++) {
    c+=x[i]+y[i];
    x[i]=c & mask;
    c>>=bpe;
  }
  for (i=k;c && i<x.length;i++) {
    c+=x[i];
    x[i]=c & mask;
    c>>=bpe;
  }
}

//do x=x*y for bigInts x and y.
//for greater speed, let y<x.
function mult(x,y) {
  var i;
  if (ss.length!=2*x.length)
    ss=new Array(2*x.length);
  copyInt(ss,0);
  for (i=0;i<y.length;i++)
    if (y[i])
      linCombShift(ss,x,y[i],i);   //ss=1*ss+y[i]*(x<<(i*bpe))
  copy(x,ss);
}

//do x=x mod n for bigInts x and n.
function mod(x,n) {
  if (s4.length!=x.length)
    s4=dup(x);
  else
    copy(s4,x);
  if (s5.length!=x.length)
    s5=dup(x);
  divide(s4,n,s5,x);  //x = remainder of s4 / n
}

//do x=x*y mod n for bigInts x,y,n.
//for greater speed, let y<x.
function multMod(x,y,n) {
  var i;
  if (s0.length!=2*x.length)
    s0=new Array(2*x.length);
  copyInt(s0,0);
  for (i=0;i<y.length;i++)
    if (y[i])
      linCombShift(s0,x,y[i],i);   //s0=1*s0+y[i]*(x<<(i*bpe))
  mod(s0,n);
  copy(x,s0);
}

//do x=x*x mod n for bigInts x,n.
function squareMod(x,n) {
  var i,j,d,c,kx,kn,k;
  for (kx=x.length; kx>0 && !x[kx-1]; kx--);  //ignore leading zeros in x
  k=kx>n.length ? 2*kx : 2*n.length; //k=# elements in the product, which is twice the elements in the larger of x and n
  if (s0.length!=k)
    s0=new Array(k);
  copyInt(s0,0);
  for (i=0;i<kx;i++) {
    c=s0[2*i]+x[i]*x[i];
    s0[2*i]=c & mask;
    c>>=bpe;
    for (j=i+1;j<kx;j++) {
      c=s0[i+j]+2*x[i]*x[j]+c;
      s0[i+j]=(c & mask);
      c>>=bpe;
    }
    s0[i+kx]=c;
  }
  mod(s0,n);
  copy(x,s0);
}

//return x with exactly k leading zeros
function trim(x,k) {
  var i,y;
  for (i=x.length; i>0 && !x[i-1]; i--);
  y=new Array(i+k);
  copy(y,x);
  return y;
}

//do x=x**y mod n, where x,y,n are bigInts (n is odd) and ** is exponentiation.  0**0=1.
function powMod(x,y,n) {
  var k1,k2,kn,np;

  //calculate np from n for the Montgomery multiplications
  for (kn=n.length;kn>0 && !n[kn-1];kn--);
  np=radix-inverseModInt(modInt(n,radix),radix);
  if(s7.length!=n.length)
    s7=dup(n);
  copyInt(s7,0);
  s7[kn]=1;
  multMod(x ,s7,n);   // x = x * 2**(kn*bp) mod n
  if (s3.length!=x.length)
    s3=dup(x);
  else
    copy(s3,x);

  for (k1=y.length-1;k1>0 & !y[k1]; k1--);  //k1=first nonzero element of y
  if (y[k1]==0) {  //anything to the 0th power is 1
    copyInt(x,1);
    return;
  }

  for (k2=1<<(bpe-1);k2 && !(y[k1] & k2); k2>>=1);  //k2=position of first 1 bit in y[k1]

  for (;;) {
    if (!(k2>>=1)) {  //look at next bit of y
      k1--;
      if (k1<0) {
        mont(x,one,n,np);
        return;
      }
      k2=1<<(bpe-1);
    }
    mont(x,x,n,np);

    if (k2 & y[k1]) //if next bit is a 1
      mont(x,s3,n,np);
  }
}

//do x=x*y*Ri mod n for bigInts x,y,n,
//  where Ri = 2**(-kn*bpe) mod n, and kn is the
//  number of elements in the n array, not
//  counting leading zeros.
//x must be large enough to hold the answer.
//It's OK if x and y are the same variable.
//must have:
//  x,y < n
//  n is odd
//  np = -(n^(-1)) mod radix
function mont(x,y,n,np) {
  var i,j,c,ui,t;
  var kn=n.length;
  var ky=y.length;

  if (sa.length!=kn)
    sa=new Array(kn);

  for (;kn>0 && n[kn-1]==0;kn--); //ignore leading zeros of n
  //this function sometimes gives wrong answers when the next line is uncommented
  //for (;ky>0 && y[ky-1]==0;ky--); //ignore leading zeros of y

  copyInt(sa,0);

  //the following loop consumes 95% of the runtime for randTruePrime() and powMod() for large keys
  for (i=0; i<kn; i++) {
    t=sa[0]+x[i]*y[0];
    ui=(t*np) & mask;
    c=(t+ui*n[0]) >> bpe;
    t=x[i];

    //do sa=(sa+x[i]*y+ui*n)/b   where b=2**bpe
    for (j=1;j<ky;j++) {
      c+=sa[j]+t*y[j]+ui*n[j];
      sa[j-1]=c & mask;
      c>>=bpe;
    }
    for (;j<kn;j++) {
      c+=sa[j]+ui*n[j];
      sa[j-1]=c & mask;
      c>>=bpe;
    }
    sa[j-1]=c & mask;
  }

  if (!greater(n,sa))
    sub(sa,n);
  copy(x,sa);
}
