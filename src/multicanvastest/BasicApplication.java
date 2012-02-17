package multicanvastest;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;

/**
 *
 * @author tomas
 */
public class BasicApplication implements ApplicationListener {

    private Mesh mesh;
    private Color color;

    public BasicApplication(Color color) {
        this.color = color;
    }

    @Override
    public void create() {
        if (mesh == null) {
            mesh = new Mesh(true, 3, 3,
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));

            mesh.setVertices(new float[]{-0.5f, -0.5f, 0,
                        0.5f, -0.5f, 0,
                        0, 0.5f, 0});
            mesh.setIndices(new short[]{0, 1, 2});
        }
    }

    @Override
    public void resize(int width, int height) {
    }
    float ticks;

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(color.r * ticks, color.g * ticks, color.b, color.a);
        mesh.render(GL10.GL_TRIANGLES, 0, 3);
        ticks += 0.1f;

        if (ticks > 1.0f) {
            ticks = 0;
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
