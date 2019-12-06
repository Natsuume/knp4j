package dev.natsuume.knp4j.data.element;

import com.google.common.collect.Collections2;
import dev.natsuume.knp4j.data.DependencyTarget;
import dev.natsuume.knp4j.data.DependencyType;
import dev.natsuume.knp4j.data.define.KnpFeature;
import dev.natsuume.knp4j.data.define.KnpParentNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import jdk.jfr.Percentage;

public class KnpClause implements KnpParentNode {
  private static final Pattern FEATURE_PATTERN = Pattern.compile("(?<=<).+(?=>)");
  private static final String INFO_SPLIT_PATTERN = " (?=<)";
  private static final String IDX_INFO_SEPARATOR = " ";

  private final List<KnpPhrase> phrases;
  private final int idx;
  private final DependencyTarget dependencyTarget;
  private final List<KnpFeature> features;
  private final List<KnpClause> dependencies;

  /**
   * 文節を表すインスタンスを生成する.
   * @param parser 文節情報を持つParser
   */
  public KnpClause(KnpClauseParser parser) {
    this.phrases = parser.phrases;
    this.idx = parser.idx;
    this.dependencyTarget = parser.dependencyTarget;
    this.dependencies = parser.dependencies;
    this.features = parser.features;
  }

  @Override
  public String getSurfaceForm() {
    return phrases.stream().map(KnpPhrase::getSurfaceForm).collect(Collectors.joining());
  }

  @Override
  public String getReadingForm() {
    return phrases.stream().map(KnpPhrase::getReadingForm).collect(Collectors.joining());
  }

  /**
   * この文節に係る文節一覧を返す.
   * @return この文節を係り先に持つ文節一覧を返す.
   */
  public List<KnpClause> getDependencies() {
    return new ArrayList<>(dependencies);
  }

  @Override
  public List<KnpFeature> getFeatures() {
    return new ArrayList<>(features);
  }

  @Override
  public int getIdx() {
    return idx;
  }

  @Override
  public List<KnpMorpheme> getMorphemes() {
    return phrases.stream()
        .map(KnpPhrase::getMorphemes)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  /**
   * この文節に対応する句一覧を返す.
   * @return 句の一覧
   */
  public List<KnpPhrase> getPhrases() {
    return new ArrayList<>(phrases);
  }

  /**
   * 係り受けを元に語順を入れ替えた表層文字列一覧を返す.
   * return 表層文字列リスト
   */
  @Deprecated
  public List<String> getDependencySurfaceForm() {
    if (dependencies.isEmpty()) {
      return List.of(getSurfaceForm());
    }
    if (dependencies.size() == 1) {
      return dependencies.get(0).getDependencySurfaceForm().stream()
          .map(string -> string + getSurfaceForm())
          .collect(Collectors.toList());
    }

    List<String> surfaceForms =
        dependencies.stream()
            .map(KnpClause::getDependencySurfaceForm)
            .flatMap(List::stream)
            .collect(Collectors.toList());

    Collection<List<String>> forms = Collections2.permutations(surfaceForms);
    System.out.println("forms: " + forms);
    return forms.stream()
        .map(list -> String.join("", list) + getSurfaceForm())
        .collect(Collectors.toList());
  }

  @Override
  public DependencyTarget getDependencyTarget() {
    return dependencyTarget;
  }

  @Override
  public int getDependencyTargetIdx() {
    return dependencyTarget.targetIdx;
  }

  @Override
  public DependencyType getDependencyType() {
    return dependencyTarget.dependencyType;
  }
}