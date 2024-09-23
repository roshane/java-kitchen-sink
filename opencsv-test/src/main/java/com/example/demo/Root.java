package com.example.demo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;
import java.util.Objects;

@JsonDeserialize(using = Root.RootDeserializer.class)
public interface Root {

    class RootDeserializer extends JsonDeserializer<Root> {
        @Override
        public Root deserialize(JsonParser p,
                                DeserializationContext ctxt) throws IOException {
            TreeNode treeNode = p.readValueAsTree();
            if (Objects.isNull(treeNode.get("entity"))) {
                return resolveA(treeNode);
            }
            return resolveB(treeNode, ctxt);
        }

        private TypeA resolveA(TreeNode treeNode) {
            String name = ((TextNode) treeNode.get("name")).textValue();
            Integer id = ((IntNode) treeNode.get("id")).intValue();
            return new TypeA(name, id);
        }

        private TypeB resolveB(TreeNode treeNode, DeserializationContext ctxt) throws IOException {
            String name = ((TextNode) treeNode.get("name")).textValue();
            Integer id = ((IntNode) treeNode.get("id")).intValue();
            Entity entity = ctxt.readTreeAsValue((ObjectNode) treeNode.get("entity"), Entity.class);
            return new TypeB(name, id, entity);
        }
    }
}
