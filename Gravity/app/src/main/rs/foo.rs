#pragma version(1)
#pragma rs java_package_name(com.example.jovan.gravity)
#pragma rs_fp_imprecise

float * ax;
float * ay;
float * avx;
float * avy;
float * af;
int  * pe;


void root(const float *v_in, float *v_out,const uint32_t *size, uint32_t x) {
    float tx, ty, d,ds, fx, fy, gc = 0.05,accx, accj;
    if(pe[x]!=1)
        return;
    for(int j = x; j < (*size);j++)
    {
        if(pe[j]!=1)
            continue;
        if(x!=j)
        {

            ax[x] = ax[x] + avx[x];
            ay[x] = ay[x] + avy[x];

            ax[j] = ax[j] + avx[j];
            ay[j] = ay[j] + avy[j];


            tx = ax[x] - ax[j];
            ty = ay[x] - ay[j];

            //d = pow(pown(tx,2) + pown(ty,2),0.5);


            //ds = pown(tx,2) + pown(ty,2);
            ds = tx*tx+ty*ty;


            d = sqrt(ds);

            fx = -tx / d;
            fy = -ty / d;

            //accx = gc * (af[j] / (pow(d,2)*0.1));
            if(af[j] > 0.0001){
                accx = gc * (af[j] / (ds*0.1));

                avx[x] = avx[x] + accx * fx;
                avy[x] = avy[x] + accx * fy;
            }
            //accj = gc * (af[x] / (pow(d,2)*0.1));
            if(af[x] > 0.0001){
                accj = gc * (af[x] / (ds*0.1));

                avx[j] = avx[j] - accj * fx;
                avy[j] = avy[j] - accj * fy;
            }

            if (d < 2 * sqrt(sqrt(af[x] * 20)))
            {
                pe[j] = 0;
                avx[x] = avx[x] * ((af[x]) / (af[x] + af[j])) + avx[j] * ((af[j]) / (af[x] + af[j]));
                avy[x] = avy[x] * ((af[x]) / (af[x] + af[j])) + avy[j] * ((af[j]) / (af[x] + af[j]));
                af[x] += af[j];
                af[j] = 0;
                avx[j] = 0;
                avy[j] = 0;
            }
        }
    }
}


void root2(const float *v_in, float *v_out,const uint32_t *size, uint32_t x) {
    float tx, ty, d,ds, fx, fy, gc = 0.05,accx, accj;

    int h = x%(*size);
    int j = x/(*size);

    if(pe[h]!=1)
        return;
    if(pe[j]!=1)
        return;
    if(h>j)
    {
        ax[h] = ax[h] + avx[h];
        ay[h] = ay[h] + avy[h];

        ax[j] = ax[j] + avx[j];
        ay[j] = ay[j] + avy[j];


        tx = ax[h] - ax[j];
        ty = ay[h] - ay[j];

        //d = pow(pown(tx,2) + pown(ty,2),0.5);


        //ds = pown(tx,2) + pown(ty,2);
        ds = tx*tx+ty*ty;


        d = sqrt(ds);

        fx = -tx / d;
        fy = -ty / d;

        //accx = gc * (af[j] / (pow(d,2)*0.1));
        accx = gc * (af[j] / (ds*0.1));

        avx[h] = avx[h] + accx * fx;
        avy[h] = avy[h] + accx * fy;

        //accj = gc * (af[x] / (pow(d,2)*0.1));
        accj = gc * (af[h] / (ds*0.1));

        avx[j] = avx[j] - accj * fx;
        avy[j] = avy[j] - accj * fy;


        if (d < 2 * (sqrt(sqrt(af[h] * 20))+sqrt(sqrt(af[j] * 20))))
        {
            pe[j] = 0;
            avx[h] = avx[h] * ((af[h]) / (af[h] + af[j])) + avx[j] * ((af[j]) / (af[h] + af[j]));
            avy[h] = avy[h] * ((af[h]) / (af[h] + af[j])) + avy[j] * ((af[j]) / (af[h] + af[j]));
            af[h] += af[j];
            af[j] = 0;
            avx[j] = 0;
            avy[j] = 0;
            ax[j] = 1000000;
            ay[j] = 1000000;
        }

    }
}