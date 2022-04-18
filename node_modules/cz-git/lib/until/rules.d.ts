/**
 * @description: fork by "@commitlint/cz-commitlint/src/utils/" v16.2.1
 */
import { RuleConfigCondition, RuleConfigSeverity } from "../share";
export declare type Rule = Readonly<[RuleConfigSeverity.Disabled]> | Readonly<[RuleConfigSeverity, RuleConfigCondition]> | Readonly<[RuleConfigSeverity, RuleConfigCondition, unknown]>;
/**
 * @description: rule is Disabled
 * @example: ruleIsDisabled([0]) => true
 * @example: ruleIsDisabled([2]) => false
 */
export declare function ruleIsDisabled(rule: Rule): rule is Readonly<[RuleConfigSeverity.Disabled]>;
/**
 * @description: rule is use
 * @example: ruleIsActive([0]) => false
 * @example: ruleIsActive([2]) => true
 */
export declare function ruleIsActive<T extends Rule>(rule: T | undefined): rule is Exclude<T, Readonly<[RuleConfigSeverity.Disabled]>>;
/**
 * @description: rule is can ignore
 */
export declare function ruleIsNotApplicable(rule: Rule): rule is Readonly<[RuleConfigSeverity, "never"]> | Readonly<[RuleConfigSeverity, "never", unknown]>;
/**
 * @description: rule is effect
 */
export declare function ruleIsApplicable(rule: Rule): rule is Readonly<[RuleConfigSeverity, "always"]> | Readonly<[RuleConfigSeverity, "always", unknown]>;
export declare function enumRuleIsActive(rule: Rule | undefined): rule is Readonly<[RuleConfigSeverity.Warning | RuleConfigSeverity.Error, "always", string[]]>;
export declare function getEnumList(rule: Rule | undefined): string[];
/**
 * @example: getMaxLength(rules['max-header-length'] => 100)
 */
export declare function getMaxLength(rule?: Rule): number;
/**
 * @example:  getMinLength(rules['min-header-length'] => 2)
 */
export declare function getMinLength(rule?: Rule): number;
