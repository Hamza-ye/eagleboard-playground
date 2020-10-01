package com.mass3d.option;

import java.util.List;

public interface OptionService {

  String ID = OptionService.class.getName();

  // -------------------------------------------------------------------------
  // OptionSet
  // -------------------------------------------------------------------------

  int saveOptionSet(OptionSet optionSet);

  void updateOptionSet(OptionSet optionSet);

  OptionSet getOptionSet(int id);

  OptionSet getOptionSet(String uid);

  OptionSet getOptionSetByName(String name);

  OptionSet getOptionSetByCode(String code);

  void deleteOptionSet(OptionSet optionSet);

  List<OptionSet> getAllOptionSets();

  List<Option> getOptions(int optionSetId, String name, Integer max);

  // -------------------------------------------------------------------------
  // Option
  // -------------------------------------------------------------------------

  void updateOption(Option option);

  Option getOption(int id);

  Option getOptionByCode(String code);

  void deleteOption(Option option);

  // -------------------------------------------------------------------------
  // OptionGroup
  // -------------------------------------------------------------------------

  int saveOptionGroup(OptionGroup group);

  void updateOptionGroup(OptionGroup group);

  OptionGroup getOptionGroup(int id);

  OptionGroup getOptionGroup(String uid);

  void deleteOptionGroup(OptionGroup group);

  List<OptionGroup> getAllOptionGroups();

  // -------------------------------------------------------------------------
  // OptionGroupSet
  // -------------------------------------------------------------------------

  int saveOptionGroupSet(OptionGroupSet group);

  void updateOptionGroupSet(OptionGroupSet group);

  OptionGroupSet getOptionGroupSet(int id);

  OptionGroupSet getOptionGroupSet(String uid);

  void deleteOptionGroupSet(OptionGroupSet group);

  List<OptionGroupSet> getAllOptionGroupSets();
}
