/**
 * Generate a type based on a key
 * @param T Type to generate
 * @param K Key to use
 * @returns
 * @example
 * ```ts
 * type MyType = GenerateType<Field, "a" | 'b'>;
 * // MyType = { a: Field, b: Field }
 * ```
 */
export type GenerateType<T, K extends string> = Record<K, T>;

/**
 * Generates a new type based on a configuration object where each key maps to a field type.
 *
 * @template T - An object type where values must be keys of FieldTypeMap
 * @returns A new type where each property of T is transformed using GenerateFieldType
 *
 * @example
 * ```ts
 * type FieldTypeMap = {
 *  user: BasicField;
 *  admin: AdminField;
 * }
 *
 * type Config = {
 *   member: 'user';
 *   leader: 'admin';
 * };
 *
 * type Result = GenerateFromConfig<FieldTypeMap, Config>;
 * // Result: { member: BasicField, leader: AdminField }
 * ```
 */
export type GenerateFromConfig<
  TypeMap extends Record<string, any>,
  Config extends Record<string, keyof TypeMap>
> = {
  [K in keyof Config]: TypeMap[Config[K]];
};
