import ObjCParser._
import collection.JavaConversions._

trait ExternalDeclarationVisitor extends Converter {
  self: ObjCBaseVisitor[String] =>

  override def visitTranslation_unit(ctx: Translation_unitContext): String = {
    concatChildResults(ctx, "")
  }

  override def visitExternal_declaration(ctx: External_declarationContext): String = {
    concatChildResults(ctx, "\n\n")
  }

  override def visitClass_interface(ctx: Class_interfaceContext): String = {
    val sb = new StringBuilder()

    // class [CLASS-NAME] : [SUPERCLASS], [PROTOCOL1, PROTOCOL2, ...]
    sb.append("class " + visit(ctx.class_name))

    Option(ctx.superclass_name) match {
      case Some(c) => sb.append(" : " + visit(c))
      case None =>
    }
    Option(ctx.protocol_reference_list) match {
      case Some(c) => sb.append(", " + visit(c))
      case None =>
    }

    // implementation of class
    sb.append(" {\n")
    Option(ctx.interface_declaration_list) match {
      case Some(c) => sb.append(visit(c))
      case None =>
    }

    findCorrespondingClassImplementation(ctx) match {
      case Some(c) =>
        visited.put(c, true)
        sb.append(visit(c.implementation_definition_list))
      case None =>
    }

    sb.append("\n}")

    sb.toString()
  }

  override def visitCategory_interface(ctx: Category_interfaceContext): String = {
    val sb = new StringBuilder()

    // extension [CLASS-NAME]
    sb.append("extension " + visit(ctx.class_name))
    Option(ctx.protocol_reference_list) match {
      case Some(c) => sb.append(", " + visit(c))
      case None =>
    }

    sb.append(" {\n")
    Option(ctx.interface_declaration_list) match {
      case Some(c) => sb.append(visit(c))
      case None =>
    }
    sb.append("}")

    sb.toString()
  }

  override def visitInterface_declaration_list(ctx: Interface_declaration_listContext): String = {
    concatChildResults(ctx, "\n")
  }

  override def visitClass_implementation(ctx: Class_implementationContext): String = {

    // TODO: Considier what to do
    //concatChildResults(ctx, "")
    ""
  }

  override def visitCategory_implementation(ctx: Category_implementationContext): String = {
    // TODO
    ""
  }

  override def visitImplementation_definition_list(ctx: Implementation_definition_listContext) = concatChildResults(ctx, "")

  override def visitClass_name(ctx: Class_nameContext) = ctx.getText
  override def visitSuperclass_name(ctx: Superclass_nameContext) = ctx.getText
  override def visitCategory_name(ctx: Category_nameContext) = ctx.getText

  override def visitProtocol_reference_list(ctx: Protocol_reference_listContext) = visit(ctx.protocol_list)
  override def visitProtocol_list(ctx: Protocol_listContext) = {
    ctx.protocol_name.map(visit).mkString(", ")
  }
  override def visitProtocol_name(ctx: Protocol_nameContext) = ctx.getText
}