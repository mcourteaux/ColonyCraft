package com.colonycraft.utilities;

public class FastUnordredArrayList<T>
{
	private FastArrayList<T> arraylist;
	private int nullCount;
	private int firstNull;

	public FastUnordredArrayList()
	{
		arraylist = new FastArrayList<T>();
		firstNull = -1;
	}

	public FastUnordredArrayList(int initialCapacity)
	{
		arraylist = new FastArrayList<T>(initialCapacity);
		firstNull = -1;
	}

	public void add(T obj)
	{
		if (nullCount == 0)
		{
			arraylist.add(obj);
		} else
		{
			arraylist.set(firstNull, obj);
			nullCount--;
			if (nullCount > 0)
			{
				for (int i = firstNull + 1; i < arraylist.size(); ++i)
				{
					if (arraylist.get(i) == null)
					{
						firstNull = i;
						break;
					}
				}
			} else
			{
				firstNull = -1;
			}
		}
	}

	public void remove(T obj)
	{
		int index = arraylist.indexOf(obj);
		nullCount++;
		arraylist.set(index, null);
		if (nullCount == 1)
			firstNull = index;
		else
			firstNull = Math.min(index, firstNull);	}

	public void remove(int index)
	{
		nullCount++;
		arraylist.set(index, null);
		if (nullCount == 1)
			firstNull = index;
		else
			firstNull = Math.min(index, firstNull);
	}

	public T get(int index)
	{
		return arraylist.get(index);
	}

	public int indexOf(T obj)
	{
		return arraylist.indexOf(obj);
	}

	public int size()
	{
		return arraylist.size();
	}

}
