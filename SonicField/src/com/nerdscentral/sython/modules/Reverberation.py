import random
import math
from Parallel_Helpers import mix,realise

@sf_parallel
def granular_reverberate(signal,ratio,delay,density,length=50,stretch=1,vol=1,rand=1.0,spread=1.0):
    c_log("Granular reverb: ratio:",ratio," delay:",delay," density",density," length:",length," stretch:",stretch," volume:",vol,"rand:",rand,"spread",spread)
    out=[]
    for grain in sf.Granulate(signal,length,10):
        (signal_i,at)=grain
        signal_i=sf.Realise(signal_i)
        signal_i=sf.Realise(sf.DirectRelength(signal_i,ratio-0.01*spread+(0.02*spread*random.random())))
        for x in range(0,density):
            time=delay*(
                (random.random()+random.random())*rand+
                (1.0-rand)
            )
            time=abs(time)
            out.append(
                (
                    +signal_i,
                    int((at + time)*stretch)
                )
            )
        -signal_i
  
    out=sf.Realise(mix(out))
    out=sf.Realise(sf.NumericVolume(out,vol))
    return out
    
@sf_parallel
def reverberate_inner(signal,convol,grain_length):
    mag=sf.Magnitude(+signal)
    if mag>0:
        signal_=sf.Concatenate(signal,sf.Silence(grain_length))
        len=sf.Length(+signal_)
        signal_=sf.FrequencyDomain(signal_)
        signal_=sf.CrossMultiply(convol,signal_)
        signal_=sf.TimeDomain(signal_)
        newMag=sf.Magnitude(+signal_)
        signal_=sf.NumericVolume(signal_,mag/newMag)        
        # tail out clicks due to amplitude at end of signal
        return sf.Realise(sf.Clean(sf.Cut(0,len,signal_)))
    else:
        -convol
        return sf.Realise(signal)

def convolve(signal,convolution):
    ls=sf.Length(+signal)
    lc=sf.Length(+convolution)
    convol_=sf.FrequencyDomain(sf.Concatenate(convolution,sf.Silence(ls)))
    return reverberate_inner(signal,convol_,lc)
    
@sf_parallel
def reverberate(signal,convol):
    c_log("Reverberate")

    grain_length = sf.Length(+convol)
    convol_=sf.FrequencyDomain(sf.Concatenate(convol,sf.Silence(grain_length)))
    signal_=sf.Concatenate(signal,sf.Silence(grain_length))
    out=[]
    for grain in sf.Granulate(signal_,grain_length):
        (signal_i,at)=grain
        signal_i=sf.Realise(signal_i)
        out.append((reverberate_inner(signal_i,+convol_,grain_length),at))
    -convol_
    return sf.Finalise(mix(out))
