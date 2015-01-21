/* For Copyright and License see LICENSE.txt and COPYING.txt in the root directory */
package com.nerdscentral.audio.pitch;

import java.util.List;

import com.nerdscentral.audio.SFSignal;
import com.nerdscentral.audio.pitch.algorithm.SFRBJFilter;
import com.nerdscentral.audio.pitch.algorithm.SFRBJFilter.FilterType;
import com.nerdscentral.sython.Caster;
import com.nerdscentral.sython.SFPL_Context;
import com.nerdscentral.sython.SFPL_Operator;
import com.nerdscentral.sython.SFPL_RuntimeException;

public class SF_ShapedBiquadPeak implements SFPL_Operator
{

    private static final long serialVersionUID = 1L;

    @Override
    public String Word()
    {
        return Messages.getString("SF_ShapedBiquadPeak.0"); //$NON-NLS-1$
    }

    @Override
    public Object Interpret(Object input, SFPL_Context context) throws SFPL_RuntimeException
    {
        List<Object> l = Caster.makeBunch(input);
        SFRBJFilter filter = new SFRBJFilter();
        try (
            SFSignal x = Caster.makeSFSignal(l.get(0));
            SFSignal frequency = Caster.makeSFSignal(l.get(1));
            SFSignal q = Caster.makeSFSignal(l.get(2)))
        {
            double db_gain = Caster.makeDouble(l.get(3));
            FilterType type = FilterType.PEAK;
            try (SFSignal y = x.replicateEmpty())
            {
                int length = y.getLength();
                for (int index = 0; index < length; ++index)
                {
                    if (index % 100 == 0)
                    {
                        filter.calc_filter_coeffs(type, frequency.getSample(index), q.getSample(index), db_gain);
                    }
                    y.setSample(index, filter.filter(x.getSample(index)));
                }
                return Caster.prep4Ret(y);
            }
        }
    }
}
