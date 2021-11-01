package xsbtReflect

import sbt._

object Import {
	val reflect			= taskKey[Seq[File]]("generated source file")
	val reflectPackage	= settingKey[String]("package name")
	val reflectClass	= settingKey[String]("class name")
}

object ReflectPlugin extends AutoPlugin {
	lazy val autoImport	= Import
	import autoImport._

	override def requires:Plugins		= plugins.JvmPlugin
	override def trigger:PluginTrigger	= noTrigger

	override def projectSettings:Seq[Def.Setting[_]]	=
		Vector(
			reflectPackage	:= "",
			reflectClass	:= "Reflect",
			reflect			:= {
				val name		= Keys.name.value
				val version		= Keys.version.value
				val packageName	= reflectPackage.value
				val className	= reflectClass.value
				val dir			= Keys.sourceManaged.value / "reflect"
				val	file		= dir / s"${className}.scala"
				val code		=
						(
							if (packageName.nonEmpty)	"package " + packageName + "\n"
							else						""
						) +
						"object " + className + " {\n" +
						"\tval name\t= \"" + name + "\"\n" +
						"\tval version\t= \"" + version + "\"\n" +
						"}\n"
				IO delete dir
				IO write (file, code)
				Seq(file)
			},
			Compile / Keys.sourceGenerators += Def.task { reflect.value }
		)
}
