/*
 * Copyright 2009 ETH Zuerich, CISD
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

package ch.systemsx.cisd.base.utilities;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import ch.systemsx.cisd.base.exceptions.CheckedExceptionTunnel;
import ch.systemsx.cisd.base.exceptions.IOExceptionUnchecked;

/**
 * Utilities for handling Java resources.
 * 
 * @author Bernd Rinn
 */
public class ResourceUtilities
{

    /**
     * Tries to copy the resource with the given name to a temporary file.
     * 
     * @param resource The name of the resource to copy.
     * @param prefix The prefix to use for the temporary name.
     * @param postfix The postfix to use for the temporary name.
     * @return The name of the temporary file, or <code>null</code>, if the resource could not be
     *         copied.
     */
    public static String tryCopyResourceToTempFile(final String resource, final String prefix,
            final String postfix)
    {
        try
        {
            return copyResourceToTempFile(resource, prefix, postfix);
        } catch (final Exception ex)
        {
            return null;
        }
    }

    /**
     * Tries to copy the resource with the given name to a temporary file.
     * 
     * @param resource The name of the resource to copy.
     * @param prefix The prefix to use for the temporary name.
     * @param postfix The postfix to use for the temporary name.
     * @param cleanUpOldResources If <code>true</code>, remove old leftover temporary files for this
     *            <var>prefix</var> and <var>postfix</var>.
     * @return The name of the temporary file, or <code>null</code>, if the resource could not be
     *         copied.
     */
    public static String tryCopyResourceToTempFile(final String resource, final String prefix,
            final String postfix, final boolean cleanUpOldResources)
    {
        try
        {
            return copyResourceToTempFile(resource, prefix, postfix, cleanUpOldResources);
        } catch (final Exception ex)
        {
            return null;
        }
    }

    /**
     * Copies the resource with the given name to a temporary file. The file will be deleted on
     * program exit.
     * 
     * @param resource The name of the resource to copy.
     * @param prefix The prefix to use for the temporary name.
     * @param postfix The postfix to use for the temporary name.
     * @return The name of the temporary file.
     * @throws IllegalArgumentException If the resource cannot be found in the class path.
     * @throws IOExceptionUnchecked If an {@link IOException} occurs.
     */
    public static String copyResourceToTempFile(final String resource, final String prefix,
            final String postfix) throws IOExceptionUnchecked
    {
        return copyResourceToTempFile(resource, prefix, postfix, false);
    }

    /**
     * Copies the resource with the given name to a temporary file. The file will be deleted on
     * program exit.
     * 
     * @param resource The name of the resource to copy.
     * @param prefix The prefix to use for the temporary name.
     * @param postfix The postfix to use for the temporary name.
     * @param cleanUpOldResources If <code>true</code>, remove old leftover temporary files for this
     *            <var>prefix</var> and <var>postfix</var>.
     * @return The name of the temporary file.
     * @throws IllegalArgumentException If the resource cannot be found in the class path.
     * @throws IOExceptionUnchecked If an {@link IOException} occurs.
     */
    public static String copyResourceToTempFile(final String resource, final String prefix,
            final String postfix, final boolean cleanUpOldResources) throws IOExceptionUnchecked
    {
        if (cleanUpOldResources)
        {
            deleteOldResourceTempFiles(prefix, postfix);
        }
        final InputStream resourceStream = ResourceUtilities.class.getResourceAsStream(resource);
        if (resourceStream == null)
        {
            throw new IllegalArgumentException("Resource '" + resource + "' not found.");
        }
        try
        {
            final File tempFile = File.createTempFile(prefix, postfix);
            tempFile.deleteOnExit();
            final OutputStream fileStream = new FileOutputStream(tempFile);
            try
            {
                IOUtils.copy(resourceStream, fileStream);
                fileStream.close();
            } finally
            {
                closeQuietly(fileStream);
            }
            return tempFile.getAbsolutePath();
        } catch (final IOException ex)
        {
            throw CheckedExceptionTunnel.wrapIfNecessary(ex);
        } finally
        {
            closeQuietly(resourceStream);
        }
    }

    private static void deleteOldResourceTempFiles(final String prefix, final String postfix)
    {
        final FilenameFilter filter = new WildcardFileFilter(prefix + "*" + postfix);
        for (File file : new File(System.getProperty("java.io.tmpdir")).listFiles(filter))
        {
            file.delete();
        }
    }

    /**
     * Closes an <code>OutputStream</code> unconditionally.
     * <p>
     * Equivalent to {@link OutputStream#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     * <p>
     * Example code:
     * <pre>
     * byte[] data = "Hello, World".getBytes();
     *
     * OutputStream out = null;
     * try {
     *     out = new FileOutputStream("foo.txt");
     *     out.write(data);
     *     out.close(); //close errors are handled
     * } catch (IOException e) {
     *     // error handling
     * } finally {
     *     IOUtils.closeQuietly(out);
     * }
     * </pre>
     *
     * <i>This is the method from commons-io IOUtil as that one is deprecated.<i>
     *
     * @param output the OutputStream to close, may be null or already closed
     */
    public static void closeQuietly(final Closeable closeable) 
    {
        try 
        {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            // ignore
        }
    }
}
