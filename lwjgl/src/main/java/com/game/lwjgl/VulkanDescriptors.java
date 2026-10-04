package com.game.lwjgl;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;

class VulkanDescriptors {
  private VulkanDescriptors(){}
  
  static long createCollisionDescriptorSetLayout(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkDescriptorSetLayoutBinding.Buffer bindings = VkDescriptorSetLayoutBinding.calloc(4, stack);
      
      // binding 0: uniform buffer
      bindings.get(0)
          .binding(0)
          .descriptorType(VK14.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
          .descriptorCount(1)
          .stageFlags(VK14.VK_SHADER_STAGE_COMPUTE_BIT);
      
      // binding 1: entities
      bindings.get(1)
          .binding(1)
          .descriptorType(VK14.VK_DESCRIPTOR_TYPE_STORAGE_BUFFER)
          .descriptorCount(1)
          .stageFlags(VK14.VK_SHADER_STAGE_COMPUTE_BIT);
      
      // binding 2: cell starts
      bindings.get(2)
          .binding(2)
          .descriptorType(VK14.VK_DESCRIPTOR_TYPE_STORAGE_BUFFER)
          .descriptorCount(1)
          .stageFlags(VK14.VK_SHADER_STAGE_COMPUTE_BIT);
      
      // binding 3: output
      bindings.get(3)
          .binding(3)
          .descriptorType(VK14.VK_DESCRIPTOR_TYPE_STORAGE_BUFFER)
          .descriptorCount(1)
          .stageFlags(VK14.VK_SHADER_STAGE_COMPUTE_BIT);
      
      VkDescriptorSetLayoutCreateInfo layoutCreateInfo = VkDescriptorSetLayoutCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
          .pBindings(bindings);
      
      long[] pDescriptorSetLayout = new long[1];
      if (VK14.vkCreateDescriptorSetLayout(lwjglData.vkDevice, layoutCreateInfo, null, pDescriptorSetLayout) != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create collision compute descriptor set layout");
      }
      
      return pDescriptorSetLayout[0];
    }
  }
  
  static long create2DDescriptorSetLayout(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkDescriptorSetLayoutBinding.Buffer b = VkDescriptorSetLayoutBinding.calloc(1, stack);
      b.get(0).binding(0)
          .descriptorType(VK14.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
          .descriptorCount(1)
          .stageFlags(VK14.VK_SHADER_STAGE_FRAGMENT_BIT);
      VkDescriptorSetLayoutCreateInfo info = VkDescriptorSetLayoutCreateInfo.calloc(stack)
          .sType$Default().pBindings(b);
      long[] out = new long[1];
      if (VK14.vkCreateDescriptorSetLayout(lwjglData.vkDevice, info, null, out) != VK14.VK_SUCCESS)
        throw new RuntimeException("Failed to create 2D descriptor set layout");
      return out[0];
    }
  }
}
