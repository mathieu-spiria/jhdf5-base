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

package ch.systemsx.cisd.base.tests;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.util.RetryAnalyzerCount;

/**
 * An {@link RetryAnalyzerCount} extension which sets the count to <code>50</code>.
 * <p>
 * This {@link IRetryAnalyzer} should only be applied to methods we know they should run
 * successfully but do not for some reason. The retry analyzer exits as soon as it made a successful
 * call.
 * </p>
 * 
 * @author Bernd Rinn
 */
public final class Retry50 extends RetryAnalyzerCount
{
    public Retry50()
    {
        setCount(50);
    }

    //
    // RetryAnalyzerCount
    //

    @Override
    public final boolean retryMethod(final ITestResult result)
    {
        return true;
    }

}
