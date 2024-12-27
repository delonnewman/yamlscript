# frozen_string_literal: true

require_relative "lib/yamlscript/version"

Gem::Specification.new do |spec|
  spec.name          = "yamlscript"
  spec.version       = YAMLScript::VERSION
  spec.authors       = ["Ingy döt Net", "Delon Newman"]
  spec.email         = ["ingy@ingy.net", "contact@delonnewman.name"]

  spec.summary       = "Program in YAML — Code is Data"
  spec.description   = "Program in YAML — Code is Data"
  spec.homepage      = "https://github.com/yaml/yamlscript"
  spec.required_ruby_version = ">= 2.4.0"

  spec.metadata["homepage_uri"] = spec.homepage
  spec.metadata["source_code_uri"] = spec.homepage
  spec.metadata["changelog_uri"] = \
    "https://github.com/yaml/yamlscript/tree/main/ruby/ChangeLog.md"

  spec.files = Dir.chdir(File.expand_path(__dir__)) do
    `git ls-files -z`.split("\x0").reject {
      |f| f.match(%r{\A(?:test|spec|features)/})
    }
  end
  spec.bindir        = "exe"
  spec.executables   = spec.files.grep(%r{\Aexe/}) { |f| File.basename(f) }
  spec.require_paths = ["lib"]

  spec.add_dependency "minitest", "~> 5.20.0"
end
