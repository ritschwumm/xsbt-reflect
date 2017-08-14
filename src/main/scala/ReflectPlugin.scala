package xsbtReflect

import sbt._

object Import {
	val reflect			= taskKey[Seq[File]]("generated source files")
	val reflectPackage	= settingKey[String]("package name")
	val reflectClass	= settingKey[String]("class name")
}

/**
sourceGenerators in Compile <+= reflect map identity
*/
object ReflectPlugin extends AutoPlugin {
	lazy val autoImport	= Import
	import autoImport._
	
	override def requires:Plugins		= plugins.JvmPlugin
	
	override def trigger:PluginTrigger	= noTrigger
	
	override def projectSettings:Seq[Def.Setting[_]]	=
			Vector(
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
