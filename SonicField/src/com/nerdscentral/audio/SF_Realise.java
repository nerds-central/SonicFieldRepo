/* For Copyright and License see LICENSE.txt and COPYING.txt in the root directory */
package com.nerdscentral.audio;

import com.nerdscentral.sython.Caster;
import com.nerdscentral.sython.SFPL_Context;
import com.nerdscentral.sython.SFPL_Operator;
import com.nerdscentral.sython.SFPL_RuntimeException;

public class SF_Realise implements SFPL_Operator, SFPL_RefPassThrough
{

    public SF_Realise()
    {
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public String Word()
    {
        return Messages.getString("SF_Realise.0"); //$NON-NLS-1$
    }

    @Override
    public Object Interpret(Object input, SFPL_Context context) throws SFPL_RuntimeException
    {
        if (input instanceof SFData)
        {
            return input;
        }
        try (SFSignal in = Caster.makeSFSignal(input); SFData ret = SFData.realise(in);)
        {
            return Caster.incrReference(ret);
        }
    }
}