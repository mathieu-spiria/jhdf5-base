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

package ch.systemsx.cisd.base.mdarray;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

/**
 * A multi-dimensional <code>int</code> array.
 * 
 * @author Bernd Rinn
 */
public final class MDIntArray extends MDAbstractArray<Integer>
{
    private static final long serialVersionUID = 1L;

    private int[] flattenedArray;

    /**
     * Creates an empty {@link MDIntArray} with the <var>dimensions</var>. Convenience method if
     * <var>dimensions</var> are available as {@code long[]}.
     */
    public MDIntArray(long[] dimensions)
    {
        this(new int[getLength(dimensions, 0)], toInt(dimensions), false);
    }

    /**
     * Creates an empty {@link MDIntArray} with the <var>dimensions</var>. If
     * <code>capacityHyperRows > dimensions[0]</code>, then it will create an array with a capacity
     * of <var>capacityHyperRows</var> hyper-rows. Convenience method if <var>dimensions</var> are
     * available as {@code long[]}.
     */
    public MDIntArray(long[] dimensions, long capacityHyperRows)
    {
        this(new int[getLength(dimensions, capacityHyperRows)], toInt(dimensions), false);
    }

    /**
     * Creates a {@link MDIntArray} from the given {@code flattenedArray} and {@code dimensions}. It
     * is checked that the arguments are compatible. Convenience method if <var>dimensions</var> are
     * available as {@code long[]}.
     */
    public MDIntArray(int[] flattenedArray, long[] dimensions)
    {
        this(flattenedArray, toInt(dimensions), true);
    }

    /**
     * Creates a {@link MDIntArray} from the given <var>flattenedArray</var> and
     * <var>dimensions</var>. If <var>checkDimensions</var> is {@code true}, it is checked that the
     * arguments are compatible. Convenience method if <var>dimensions</var> are available as
     * {@code long[]}.
     */
    public MDIntArray(int[] flattenedArray, long[] dimensions, boolean checkdimensions)
    {
        this(flattenedArray, toInt(dimensions), checkdimensions);
    }

    /**
     * Creates an empty {@link MDIntArray} with the <var>dimensions</var>.
     */
    public MDIntArray(int[] dimensions)
    {
        this(new int[getLength(dimensions, 0)], dimensions, false);
    }

    /**
     * Creates an empty {@link MDIntArray} with the <var>dimensions</var>. If
     * <code>capacityHyperRows > dimensions[0]</code>, then it will create an array with a capacity
     * of <var>capacityHyperRows</var> hyper-rows.
     */
    public MDIntArray(int[] dimensions, int capacityHyperRows)
    {
        this(new int[getLength(dimensions, capacityHyperRows)], dimensions, false);
    }

    /**
     * Creates a {@link MDIntArray} from the given {@code flattenedArray} and {@code dimensions}. It
     * is checked that the arguments are compatible.
     */
    public MDIntArray(int[] flattenedArray, int[] dimensions)
    {
        this(flattenedArray, dimensions, true);
    }

    /**
     * Creates a {@link MDIntArray} from the given <var>flattenedArray</var> and
     * <var>dimensions</var>. If <var>checkDimensions</var> is {@code true}, it is checked that the
     * arguments are compatible.
     */
    public MDIntArray(int[] flattenedArray, int[] dimensions, boolean checkdimensions)
    {
        super(dimensions, flattenedArray.length, 0);
        assert flattenedArray != null;

        if (checkdimensions)
        {
            final int expectedLength = getLength(dimensions, 0);
            if (flattenedArray.length != expectedLength)
            {
                throw new IllegalArgumentException("Actual array length " + flattenedArray.length
                        + " does not match expected length " + expectedLength + ".");
            }
        }
        this.flattenedArray = flattenedArray;
    }

    /**
     * Creates a {@link MDIntArray} from the given <var>matrix</var> of rank 2. Note that the values
     * in <var>matrix</var> will be copied and thus the created {@link MDIntArray} will be
     * independent from <var>matrix</var> after construction.
     */
    public MDIntArray(int[][] matrix)
    {
        this(matrix, getDimensions(matrix));
    }

    /**
     * Creates a {@link MDIntArray} from the given <var>matrix</var> of rank 2 and the
     * <var>dimension</var> which need to be less or equal the dimensions of <var>matrix</var>. Note
     * that the values in <var>matrix</var> will be copied and thus the created {@link MDIntArray}
     * will be independent from <var>matrix</var> after construction.
     */
    public MDIntArray(int[][] matrix, int[] dimensions)
    {
        super(dimensions, 0, matrix.length);

        final int sizeX = dimensions[0];
        final int sizeY = dimensions[1];
        int length = getLength(dimensions, 0);
        this.flattenedArray = new int[length];
        for (int i = 0; i < sizeX; ++i)
        {
            System.arraycopy(matrix[i], 0, flattenedArray, i * sizeY, sizeY);
        }
    }

    private static int[] getDimensions(int[][] matrix)
    {
        assert matrix != null;

        return new int[]
            { matrix.length, matrix.length == 0 ? 0 : matrix[0].length };
    }

    @Override
    public int capacity()
    {
        return flattenedArray.length;
    }

    @Override
    public Integer getAsObject(int... indices)
    {
        return get(indices);
    }

    @Override
    public void setToObject(Integer value, int... indices)
    {
        set(value, indices);
    }

    @Override
    public Integer getAsObject(int linearIndex)
    {
        return get(linearIndex);
    }

    @Override
    public void setToObject(Integer value, int linearIndex)
    {
        set(value, linearIndex);
    }

    @Override
    public int[] getAsFlatArray()
    {
        return flattenedArray;
    }

    @Override
    public int[] getCopyAsFlatArray()
    {
        return ArrayUtils.subarray(flattenedArray, 0, dimensions[0] * hyperRowLength);
    }

    @Override
    protected void adaptCapacityHyperRows()
    {
        final int[] oldArray = this.flattenedArray;
        this.flattenedArray = new int[capacityHyperRows * hyperRowLength];
        System.arraycopy(oldArray, 0, flattenedArray, 0,
                Math.min(oldArray.length, flattenedArray.length));
    }

    /**
     * Returns the value of array at the position defined by <var>indices</var>.
     */
    public int get(int... indices)
    {
        return flattenedArray[computeIndex(indices)];
    }

    /**
     * Returns the value of a one-dimensional array at the position defined by <var>index</var>.
     * <p>
     * <b>Do not call for arrays other than one-dimensional!</b>
     */
    public int get(int index)
    {
        return flattenedArray[index];
    }

    /**
     * Returns the value of a two-dimensional array at the position defined by <var>indexX</var> and
     * <var>indexY</var>.
     * <p>
     * <b>Do not call for arrays other than two-dimensional!</b>
     */
    public int get(int indexX, int indexY)
    {
        return flattenedArray[computeIndex(indexX, indexY)];
    }

    /**
     * Returns the value of a three-dimensional array at the position defined by <var>indexX</var>,
     * <var>indexY</var> and <var>indexZ</var>.
     * <p>
     * <b>Do not call for arrays other than three-dimensional!</b>
     */
    public int get(int indexX, int indexY, int indexZ)
    {
        return flattenedArray[computeIndex(indexX, indexY, indexZ)];
    }

    /**
     * Sets the <var>value</var> of array at the position defined by <var>indices</var>.
     */
    public void set(int value, int... indices)
    {
        flattenedArray[computeIndex(indices)] = value;
    }

    /**
     * Sets the <var>value</var> of a one-dimension array at the position defined by
     * <var>index</var>.
     * <p>
     * <b>Do not call for arrays other than one-dimensional!</b>
     */
    public void set(int value, int index)
    {
        flattenedArray[index] = value;
    }

    /**
     * Sets the <var>value</var> of a two-dimensional array at the position defined by
     * <var>indexX</var> and <var>indexY</var>.
     * <p>
     * <b>Do not call for arrays other than two-dimensional!</b>
     */
    public void set(int value, int indexX, int indexY)
    {
        flattenedArray[computeIndex(indexX, indexY)] = value;
    }

    /**
     * Sets the <var>value</var> of a three-dimensional array at the position defined by
     * <var>indexX</var>, <var>indexY</var> and <var>indexZ</var>.
     * <p>
     * <b>Do not call for arrays other than three-dimensional!</b>
     */
    public void set(int value, int indexX, int indexY, int indexZ)
    {
        flattenedArray[computeIndex(indexX, indexY, indexZ)] = value;
    }

    /**
     * Creates and returns a matrix from a two-dimensional array.
     * <p>
     * <b>Do not call for arrays other than two-dimensional!</b>
     */
    public int[][] toMatrix()
    {
        final int sizeX = dimensions[0];
        final int sizeY = dimensions[1];
        final int[][] result = new int[sizeX][sizeY];
        for (int i = 0; i < sizeX; ++i)
        {
            System.arraycopy(flattenedArray, i * sizeY, result[i], 0, sizeY);
        }
        return result;
    }

    //
    // Object
    //

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(getValuesAsFlatArray());
        result = prime * result + Arrays.hashCode(dimensions);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        MDIntArray other = (MDIntArray) obj;
        if (Arrays.equals(getValuesAsFlatArray(), other.getValuesAsFlatArray()) == false)
        {
            return false;
        }
        if (Arrays.equals(dimensions, other.dimensions) == false)
        {
            return false;
        }
        return true;
    }

    private int[] getValuesAsFlatArray()
    {
        return (dimensions[0] < capacityHyperRows) ? getCopyAsFlatArray() : getAsFlatArray(); 
    }
    
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
    {
        stream.defaultReadObject();
        if (hyperRowLength == 0)
        {
            this.hyperRowLength = computeHyperRowLength(dimensions);
        }
        if (capacityHyperRows == 0)
        {
            this.capacityHyperRows = dimensions[0];
        }
        if (size == 0)
        {
            this.size = hyperRowLength * dimensions[0];
        }
    }

}
