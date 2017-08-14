import sbt._

/**
usage
	<code>
	reflectSettings
	
	sourceGenerators in Compile <+= reflect map identity
	</code>
code
	println("project version=" + Reflect.version)
*/
object ReflectPlugin extends Plugin {
	val reflect			= taskKey[Seq[File]]("generated source files")
	val reflectPackage	= settingKey[String]("package name")
	val reflectClass	= settingKey[String]("class name")
	
	lazy val reflectSettings:Seq[Def.Setting[_]]	=
			Seq(
				reflectPackage	:= "",
				reflectClass	:= "Reflect",
				reflect			<<= (Keys.sourceManaged, Keys.name, Keys.version, reflectPackage, reflectClass) map {
					(sourceManaged:File, name:String, version:String, reflectPackage:String, reflectClass:String)	=>
						val	file	= sourceManaged / "reflect" / "Reflect.scala"
						val code	= 
								(
									if (reflectPackage.nonEmpty)	"package " + reflectPackage + "\n"
									else							""
								) +
								"object " + reflectClass + " {\n" + 
								"\tval name\t= \"" + name + "\"\n" + 
								"\tval version\t= \"" + version + "\"\n" + 
								"}\n"  
						IO write (file, code)
						Seq(file)
				}
			)
}
