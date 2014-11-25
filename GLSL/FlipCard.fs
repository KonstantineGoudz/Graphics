precision mediump float;
uniform vec4 vColor;
uniform sampler2D u_Texture;   
varying float Scale;

varying float uvx;
varying float uvy;

varying vec2 a_uvCoord;

void main() {

vec2 uv=a_uvCoord;

//if the back of the card is facing the camera change the uv coordinates
//so that the back of the card shows a different image
if(!gl_FrontFacing)
{
uv.x=uv.x+uvx;
uv.y=uv.y+uvy;
}

 vec4 col = texture2D(u_Texture, uv);
 gl_FragColor =col;// col2*fac1+col*fac2;}

// discard pixel if the alpha value is below 1.0 
 if(gl_FragColor.a<1.0)
 discard;

}
