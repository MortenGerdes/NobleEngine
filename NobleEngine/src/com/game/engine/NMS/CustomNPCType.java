package com.game.engine.NMS;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_8_R2.BiomeBase;
import net.minecraft.server.v1_8_R2.BiomeBase.BiomeMeta;
import net.minecraft.server.v1_8_R2.EntityInsentient;
import net.minecraft.server.v1_8_R2.EntityTypes;
import net.minecraft.server.v1_8_R2.EntityZombie;

import org.bukkit.entity.EntityType;

import com.game.engine.NMS.types.customZombie;

public enum CustomNPCType
{
	CUSTOMZOMBIE("Zombie", 54, EntityType.ZOMBIE, EntityZombie.class, customZombie.class);
	private String name;
	private int id;
	private EntityType entityType;
	private Class<? extends EntityInsentient> nmsClass;
	private Class<? extends EntityInsentient> customClass;

	private CustomNPCType(String name, int id, EntityType entityType, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass)
	{
		this.name = name;
		this.id = id;
		this.entityType = entityType;
		this.nmsClass = nmsClass;
		this.customClass = customClass;
	}

	public static void registerEntities()
	{
		for (CustomNPCType entity : values())
		{
			insertData(entity.getCustomClass(), entity.getName(), entity.id);
			BiomeBase[] biomes = (BiomeBase[]) getPrivateStaticField(BiomeBase.class, "biomes");
			for (BiomeBase biome : biomes)
			{
				if (biome == null)
				{
					break;
				}
				for (String fields : new String[]
				{ "at", "au", "av", "aw" })
				{
					Field list;
					try
					{
						list = BiomeBase.class.getDeclaredField(fields);
						list.setAccessible(true);
						@SuppressWarnings("unchecked")
						List<BiomeMeta> moblist = ((List<BiomeMeta>) list.get(biome));
						for (BiomeMeta meta : moblist)
						{
							for (CustomNPCType entities : values())
							{
								if (entities.getNmsClass().equals(meta.b))
								{
									meta.b = entities.getCustomClass();
								}
							}
						}
					}
					catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static void unRegisterEntities()
	{
		for (CustomNPCType entity : values())
		{
			((Map) getPrivateStaticField(EntityTypes.class, "d")).remove(entity.getCustomClass());
			((Map) getPrivateStaticField(EntityTypes.class, "f")).remove(entity.getCustomClass());
		}
		for (CustomNPCType entity : values())
		{
			insertData(entity.getNmsClass(), entity.getName(), entity.getId());
		}
		BiomeBase[] biomes = (BiomeBase[]) getPrivateStaticField(BiomeBase.class, "biomes");
		for (BiomeBase biome : biomes)
		{
			if (biome == null)
			{
				break;
			}
			for (String fields : new String[]
			{  "at", "au", "av", "aw" })
			{
				Field list;
				try
				{
					list = BiomeBase.class.getDeclaredField(fields);
					list.setAccessible(true);
					@SuppressWarnings("unchecked")
					List<BiomeMeta> moblist = (List<BiomeMeta>) list.get(biome);
					for (BiomeMeta meta : moblist)
					{
						for (CustomNPCType entities : values())
						{
							if (entities.getNmsClass().equals(meta.b))
							{
								meta.b = entities.getNmsClass();
							}
						}
					}
				}
				catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private static void insertData(Class paramClass, String paramString, int paramInt)
	{
		((Map) getPrivateStaticField(EntityTypes.class, "c")).put(paramString, paramClass);
		((Map) getPrivateStaticField(EntityTypes.class, "d")).put(paramClass, paramString);
		((Map) getPrivateStaticField(EntityTypes.class, "e")).put(Integer.valueOf(paramInt), paramClass);
		((Map) getPrivateStaticField(EntityTypes.class, "f")).put(paramClass, Integer.valueOf(paramInt));
		((Map) getPrivateStaticField(EntityTypes.class, "g")).put(paramString, Integer.valueOf(paramInt));
	}

	@SuppressWarnings("rawtypes")
	private static Object getPrivateStaticField(Class clazz, String field)
	{
		try
		{
			Field theField = clazz.getDeclaredField(field);
			theField.setAccessible(true);
			return theField.get(null);
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public String getName()
	{
		return name;
	}

	public int getId()
	{
		return id;
	}

	public EntityType getEntityType()
	{
		return entityType;
	}

	public Class<? extends EntityInsentient> getNmsClass()
	{
		return nmsClass;
	}

	public Class<? extends EntityInsentient> getCustomClass()
	{
		return customClass;
	}
}
