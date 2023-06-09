/*
 * Copyright 2008 ETH Zuerich, CISD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.systemsx.cisd.base.exceptions;

/**
 * Exception that signals that whoever gets it should stop its current work. This is an unchecked
 * equivalent to an {@link InterruptedException}.
 * <p>
 * This is usually triggered by interrupting the thread that the work package is processed in and
 * regularly checking with {@link #check()}.
 * </p>
 * 
 * @author Bernd Rinn
 */
public class InterruptedExceptionUnchecked extends CheckedExceptionTunnel
{

    private static final long serialVersionUID = 1L;

    public InterruptedExceptionUnchecked()
    {
        super();
    }

    public InterruptedExceptionUnchecked(final InterruptedException cause)
    {
        super(cause);
    }

    /**
     * Checks whether the current thread has been interrupted and, if it has, throw a
     * {@link InterruptedExceptionUnchecked}.
     */
    public final static void check() throws InterruptedExceptionUnchecked
    {
        if (Thread.interrupted())
        {
            throw new InterruptedExceptionUnchecked();
        }
    }

    @Override
    public InterruptedException getCause()
    {
        return (InterruptedException) super.getCause();
    }
}
