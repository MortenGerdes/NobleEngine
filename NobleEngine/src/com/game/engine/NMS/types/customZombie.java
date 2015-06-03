package com.game.engine.NMS.types;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.server.v1_8_R2.EntityHuman;
import net.minecraft.server.v1_8_R2.EntityZombie;
import net.minecraft.server.v1_8_R2.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R2.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R2.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_8_R2.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R2.World;

import org.bukkit.Location;

import com.game.engine.GameEngine;


public class customZombie extends EntityZombie
{
	public Location location;
	
	public customZombie(World world, Location location)
	{
		super(world);
		this.location = location;
		Field bField;
		try
		{
			GameEngine.Debug("Zombie constructor");
//			bField = PathfinderGoalSelector.class.getDeclaredField("b");
//			bField.setAccessible(true);
//			Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
//			cField.setAccessible(true);
//			bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
//			bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
//			cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
//			cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
			
	        List goalB = (List)getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
	        List goalC = (List)getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
	        List targetB = (List)getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
	        List targetC = (List)getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();

		}
		catch (SecurityException | IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.goalSelector.a(0, new PathfinderGoalFloat(this));
		this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 0D));
		this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
	}
	
	public Location getLocation()
	{
		return location;
	}
	
    public static Object getPrivateField(String fieldName, Class clazz, Object object)
    {
        Field field;
        Object o = null;
        try
        {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        }
        catch(NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return o;
    }
}
