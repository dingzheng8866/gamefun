package com.tiny.game.common.server.fight.domain;

public class Vector3
{
	public float x = 0;
	public float y = 0;
	public float z = 0;
	

	public static Vector3 Zero()
	{
		return new Vector3(0, 0, 0);
	}
	
	public static Vector3 One()
	{
		return new Vector3(1, 1, 1);
	}
	
	public static Vector3 Up()
	{
		return new Vector3(0, 1, 0);
	}
	
	public static Vector3 Down()
	{
		return new Vector3(0, -1, 0);
	}
	
	public static Vector3 Left()
	{
		return new Vector3(-1, 0, 0);
	}
	
	public static Vector3 Right()
	{
		return new Vector3(1, 0, 0);
	}
	
	public static Vector3 Forward()
	{
		return new Vector3(0, 0, 1);
	}
	
	public static Vector3 Back()
	{
		return new Vector3(0, 0, -1);
	}
	
	
	public Vector3()
	{
		
	}
	
	public Vector3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3 set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public Vector3 set(Vector3 val)
	{
		this.x = val.x;
		this.y = val.y;
		this.z = val.z;
		return this;
	}
	
	@Override
	public Vector3 clone()
	{
		return new Vector3(this.x, this.y, this.z);
	}
	
	public float sqrMagnitude()
	{
		return x * x   +   y * y   +   z * z;
	}
	
	public float magnitude()
	{
		return (float) Math.sqrt(x * x 		+		 y * y  		+		 z * z);
	}
	
	public Vector3 setNormalize()
	{
		float length = magnitude();
		if(length == 1)
		{
			return this;
		}
		else if(length > 1e-05)
		{
			div(length);
		}
		else
		{
			set(0, 0, 0);
		}
		
		return this;
	}
	
	public Vector3 normalize()
	{
		return this.clone().setNormalize();
	}
	
	public Vector3 div(float val)
	{
		this.x /= val;
		this.y /= val;
		this.z /= val;
		return this;
	}
	
	public Vector3 mul(float val)
	{
		this.x *= val;
		this.y *= val;
		this.z *= val;
		return this;
	}
	
	public Vector3 add(Vector3 val)
	{
		this.x += val.x;
		this.y += val.y;
		this.z += val.z;
		return this;
	}
	
	public Vector3 sub(Vector3 val)
	{
		this.x -= val.x;
		this.y -= val.y;
		this.z -= val.z;
		return this;
	}
	
	
	@Override
	public String toString()
	{
		return String.format("Vector3(%f, %f %f)", x, y, z);
	}
	
	public static Vector3 Div(Vector3 a, float b)
	{
		return a.clone().div(b);
	}
	
	public static Vector3 Mul(Vector3 a, float b)
	{
		return a.clone().mul(b);
	}
	
	public static Vector3 Add(Vector3 a, Vector3 b)
	{
		return a.clone().add(b);
	}
	
	public static Vector3 Sub(Vector3 a, Vector3 b)
	{
		return a.clone().sub(b);
	}
	
	public static float Distance(Vector3 a, Vector3 b)
	{
		return (float) Math.sqrt(Math.pow(a.x - b.x , 2) + Math.pow(a.y - b.y , 2) + Math.pow(a.z - b.z , 2));
	}
	
	public static float Dot(Vector3 a, Vector3 b)
	{
		return a.x * b.x   +   a.y * b.y    +  a.z * b.z;
	}
	
	public static Vector3 Lerp(Vector3 from, Vector3 to, float t)
	{
		t = Mathf.clamp(t, 0, 1);
		return new Vector3(
				from.x + (to.x - from.x) * t,
				from.y + (to.y - from.y) * t,
				from.z + (to.z - from.z) * t
				);
	}
	
	public static Vector3 Max(Vector3 a, Vector3 b)
	{
		return new Vector3(
				Math.max(a.x, b.x),
				Math.max(a.y, b.y),
				Math.max(a.z, b.z)
				);
	}

	
	public static Vector3 Min(Vector3 a, Vector3 b)
	{
		return new Vector3(
				Math.min(a.x, b.x),
				Math.min(a.y, b.y),
				Math.min(a.z, b.z)
				);
	}
	
	
	public static float Angle(Vector3 from, Vector3 to)
	{
		return (float) (Math.acos( Mathf.clamp( Dot(from.normalize(), to.normalize()), -1, 1) ) * Mathf.rad2Deg);
	}
	

	public static Vector3 Cross(Vector3 lhs, Vector3 rhs)
	{
		Vector3 r = Vector3.Zero();
		r.x = lhs.y * rhs.z - lhs.z * rhs.y;
		r.y = lhs.z * rhs.x - lhs.x * rhs.z;
		r.z = lhs.x * rhs.y - lhs.y * rhs.x;
		return r;
		
	}
	
}