package com.game.engine.Util;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkBuilder
{

	FireworkEffect effect;

	public void BuildFirework(Color ColorOfFirework, Type TypeOfFirework, Boolean withFlicker, Boolean withTrail)
	{
		Builder builder = FireworkEffect.builder();

		builder.withColor(ColorOfFirework);
		builder.with(TypeOfFirework);

		if (withFlicker = true)
		{
			builder.withFlicker();
		}
		if (withTrail = true)
		{
			builder.withTrail();
		}
		effect = builder.build();
	}

	public void SpawnFirework(Location location, int power)
	{
		Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
		FireworkMeta fireworkMeta = firework.getFireworkMeta();

		fireworkMeta.clearEffects();
		fireworkMeta.addEffect(effect);
		fireworkMeta.setPower(power);
		firework.setFireworkMeta(fireworkMeta);
	}
}
