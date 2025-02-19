package org.paracite.glframework;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import org.paracite.glframework.core.FileIO;
import org.paracite.glframework.core.Game;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class Texture {
	Game game;
	String fileName;
	int textureId;
	int minFilter;
	int magFilter;
	float width;
	float height;
	
	public Texture(Game game, String fileName) {
		this.game = game;
		this.fileName = fileName;
		load();
	}
	
	private void load() {
		GL10 gl = game.graphics.gl;
		FileIO fileIO = game.fileIO;
		
		int[] textureIds = new int[1];
		gl.glGenTextures(1, textureIds, 0);
		textureId = textureIds[0];
		InputStream in = null;
		try {
			in = fileIO.readAsset(fileName);
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			height = bitmap.getHeight();
			width = bitmap.getWidth();
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			setFilters(GL10.GL_NEAREST, GL10.GL_NEAREST);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load texture '" + fileName	+ "'", e);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}
	}
	
	public void reload() {
		GL10 gl = game.graphics.gl;
		load();
		bind();
		setFilters(minFilter, magFilter);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
	}
	
	public void setFilters(int minFilter, int magFilter) {
		this.minFilter = minFilter;
		this.magFilter = magFilter;
		GL10 gl = game.graphics.gl;
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, minFilter);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, magFilter);
	}
		
	public void bind() {
		GL10 gl = game.graphics.gl;
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
	}
		
	public void dispose() {
		GL10 gl = game.graphics.gl;
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		int[] textureIds = { textureId };
		gl.glDeleteTextures(1, textureIds, 0);
	}
}
